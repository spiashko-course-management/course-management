import axios from 'axios';
import {createAsyncThunk, isFulfilled, isPending} from '@reduxjs/toolkit';

import {cleanEntity} from 'app/shared/util/entity-utils';
import {createEntitySlice, EntityState, IGetListQueryParams, serializeAxiosError} from 'app/shared/reducers/reducer.utils';
import {defaultValue, IEnrollment} from 'app/shared/model/enrollment.model';

const initialState: EntityState<IEnrollment> = {
  loading: false,
  errorMessage: null,
  entities: [],
  entity: defaultValue,
  updating: false,
  updateSuccess: false,
};

const apiUrl = 'api/enrollments';

// Actions

export const getEntities = createAsyncThunk('enrollment/fetch_entity_list', async ({ page, size, sort }: IGetListQueryParams) => {
  const requestUrl = `${apiUrl}?cacheBuster=${new Date().getTime()}`;
  return axios.get<IEnrollment[]>(requestUrl);
});

export const getEntity = createAsyncThunk(
  'enrollment/fetch_entity',
  async (id: string | number) => {
    const requestUrl = `${apiUrl}/${id}`;
    return axios.get<IEnrollment>(requestUrl);
  },
  { serializeError: serializeAxiosError }
);

export const createEntity = createAsyncThunk(
  'enrollment/create_entity',
  async (entity: IEnrollment) => {
    return await axios.post<IEnrollment>(apiUrl, cleanEntity(entity));
  },
  { serializeError: serializeAxiosError }
);

export const updateEntity = createAsyncThunk(
  'enrollment/update_entity',
  async (entity: IEnrollment) => {
    return await axios.put<IEnrollment>(`${apiUrl}/${entity.id}`, cleanEntity(entity));
  },
  { serializeError: serializeAxiosError }
);

export const partialUpdateEntity = createAsyncThunk(
  'enrollment/partial_update_entity',
  async (entity: IEnrollment) => {
    return await axios.patch<IEnrollment>(`${apiUrl}/${entity.id}`, cleanEntity(entity));
  },
  { serializeError: serializeAxiosError }
);

export const deleteEntity = createAsyncThunk(
  'enrollment/delete_entity',
  async (id: string | number) => {
    const requestUrl = `${apiUrl}/${id}`;
    return await axios.delete<IEnrollment>(requestUrl);
  },
  { serializeError: serializeAxiosError }
);

// slice

export const EnrollmentSlice = createEntitySlice({
  name: 'enrollment',
  initialState,
  extraReducers(builder) {
    builder
      .addCase(getEntity.fulfilled, (state, action) => {
        state.loading = false;
        state.entity = action.payload.data;
      })
      .addCase(deleteEntity.fulfilled, state => {
        state.updating = false;
        state.updateSuccess = true;
        state.entity = {};
      })
      .addMatcher(isFulfilled(getEntities), (state, action) => {
        const { data } = action.payload;

        return {
          ...state,
          loading: false,
          entities: data,
        };
      })
      .addMatcher(isFulfilled(createEntity, updateEntity, partialUpdateEntity), (state, action) => {
        state.updating = false;
        state.loading = false;
        state.updateSuccess = true;
        state.entity = action.payload.data;
      })
      .addMatcher(isPending(getEntities, getEntity), state => {
        state.errorMessage = null;
        state.updateSuccess = false;
        state.loading = true;
      })
      .addMatcher(isPending(createEntity, updateEntity, partialUpdateEntity, deleteEntity), state => {
        state.errorMessage = null;
        state.updateSuccess = false;
        state.updating = true;
      });
  },
});

export const { reset } = EnrollmentSlice.actions;

// Reducer
export default EnrollmentSlice.reducer;
