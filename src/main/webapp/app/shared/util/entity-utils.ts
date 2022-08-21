import pick from 'lodash/pick';
import {IPaginationBaseState} from 'react-jhipster';

/**
 * Removes fields with an 'id' field that equals ''.
 * This function was created to prevent entities to be sent to
 * the server with an empty id and thus resulting in a 500.
 *
 * @param entity Object to clean.
 */
export const cleanEntity = entity => {
  const keysToKeep = Object.keys(entity).filter(k => !(entity[k] instanceof Object) || (entity[k]['id'] !== '' && entity[k]['id'] !== -1));

  return pick(entity, keysToKeep);
};

/**
 * Simply map a list of element to a list a object with the element as id.
 *
 * @param idList Elements to map.
 * @returns The list of objects with mapped ids.
 */
export const mapIdList = (idList: ReadonlyArray<any>) => idList.filter((id: any) => id !== '').map((id: any) => ({id}));

export const overridePaginationStateWithQueryParams = (paginationBaseState: IPaginationBaseState, locationSearch: string) => {
  const params = new URLSearchParams(locationSearch);
  const page = params.get('page');
  const sort = params.get('sort');
  if (page && sort) {
    const sortSplit = sort.split(',');
    paginationBaseState.activePage = +page;
    paginationBaseState.sort = sortSplit[0];
    paginationBaseState.order = sortSplit[1];
  }
  return paginationBaseState;
};

export const overrideCmPaginationStateWithQueryParams = (cmPaginationBaseState: ICmPaginationBaseState, locationSearch: string) => {
  const params = new URLSearchParams(locationSearch);
  const page = params.get('page');
  const sort = params.get('sort');
  if (page && sort) {
    cmPaginationBaseState.activePage = +page;
    cmPaginationBaseState.sort = sort;
  }
  return cmPaginationBaseState;
};

export const getFromQueryParams = (locationSearch: string, param: string) => {
  const params = new URLSearchParams(locationSearch);
  const paramValue = params.get(param);
  if (paramValue) {
    return paramValue;
  }
  return '';
};

export interface ICmPaginationBaseState {
  itemsPerPage: number;
  sort: string;
  activePage: number;
}

export const getCmSortState = (location: { search: string }, itemsPerPage: number, sortField = 'id,asc'): ICmPaginationBaseState => {
  const pageParam = getFromQueryParams(location.search, 'page');
  const sortParam = getFromQueryParams(location.search, 'sort');
  let sort = sortField;
  let activePage = 1;
  if (pageParam !== '' && !isNaN(parseInt(pageParam, 10))) {
    activePage = parseInt(pageParam, 10);
  }
  if (sortParam !== '') {
    sort = sortParam;
  }
  return {itemsPerPage, sort, activePage};
};

