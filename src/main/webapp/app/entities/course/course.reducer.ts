import axios from 'axios';
import {createAsyncThunk, isFulfilled, isPending} from '@reduxjs/toolkit';

import {cleanEntity} from 'app/shared/util/entity-utils';
import {createEntitySlice, EntityState, IQueryParams, serializeAxiosError} from 'app/shared/reducers/reducer.utils';
import {defaultValue, ICourse} from 'app/shared/model/course.model';

const initialState: EntityState<ICourse> = {
  loading: false,
  errorMessage: null,
  entities: [],
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};

const apiUrl = 'api/courses';

// Actions

export const getEntities = createAsyncThunk(
  'course/fetch_entity_list',
  async ({include, filter, page, size, sort}: IQueryParams) => {
    const requestUrl = `${apiUrl}?` +
      `${include ? `include=${include}&` : ''}` +
      `${filter ? `filter=${filter}&` : ''}` +
      `${page ? `page=${page}&` : ''}` +
      `${size ? `size=${size}&` : ''}` +
      `${sort ? `sort=${sort}&` : ''}` +
      `cacheBuster=${new Date().getTime()}`;
    return axios.get<ICourse[]>(requestUrl);
  });

export const getEntity = createAsyncThunk(
  'course/fetch_entity',
  async (id: string | number) => {
    const requestUrl = `${apiUrl}/${id}`;
    return axios.get<ICourse>(requestUrl);
  },
  { serializeError: serializeAxiosError }
);

export const createEntity = createAsyncThunk(
  'course/create_entity',
  async (entity: ICourse) => {
    return await axios.post<ICourse>(apiUrl, cleanEntity(entity));
  },
  { serializeError: serializeAxiosError }
);

export const updateEntity = createAsyncThunk(
  'course/update_entity',
  async (entity: ICourse) => {
    return await axios.put<ICourse>(`${apiUrl}/${entity.id}`, cleanEntity(entity));
  },
  { serializeError: serializeAxiosError }
);

export const partialUpdateEntity = createAsyncThunk(
  'course/partial_update_entity',
  async (entity: ICourse) => {
    return await axios.patch<ICourse>(`${apiUrl}/${entity.id}`, cleanEntity(entity));
  },
  { serializeError: serializeAxiosError }
);

export const deleteEntity = createAsyncThunk(
  'course/delete_entity',
  async (id: string | number) => {
    const requestUrl = `${apiUrl}/${id}`;
    return await axios.delete<ICourse>(requestUrl);
  },
  { serializeError: serializeAxiosError }
);

// slice

export const CourseSlice = createEntitySlice({
  name: 'course',
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
        const { data, headers } = action.payload;

        return {
          ...state,
          loading: false,
          entities: data,
          totalItems: parseInt(headers['x-total-count'], 10),
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

export const { reset } = CourseSlice.actions;

// Reducer
export default CourseSlice.reducer;
