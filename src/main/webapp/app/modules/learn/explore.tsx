import React, {useEffect, useState} from 'react';
import {Link, RouteComponentProps} from 'react-router-dom';

import {useAppDispatch, useAppSelector} from "app/config/store";
import {
  getCmSortState,
  getFromQueryParams,
  overrideCmPaginationStateWithQueryParams
} from "app/shared/util/entity-utils";
import {ITEMS_PER_PAGE, SORT} from "app/shared/util/pagination.constants";
import {getEntities} from "app/entities/course/course.reducer";
import {Button, Col, Form, FormGroup, Input, InputGroup, Row, Table} from "reactstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {JhiItemCount, JhiPagination} from "react-jhipster";

export const Explore = (props: RouteComponentProps<{ url: string }>) => {

  const dispatch = useAppDispatch();

  const [filterState, setFilterState] = useState(
    getFromQueryParams(props.location.search, 'filter')
  );
  const [filterFloatingState, setFilterFloatingState] = useState(
    getFromQueryParams(props.location.search, 'filter')
  );
  const [customSortFloatingState, setCustomSortFloatingState] = useState(
    getFromQueryParams(props.location.search, 'sort')
  );
  const [paginationState, setPaginationState] = useState(
    overrideCmPaginationStateWithQueryParams(getCmSortState(props.location, ITEMS_PER_PAGE, 'id,asc'), props.location.search)
  );

  const courseList = useAppSelector(state => state.course.entities);
  const loading = useAppSelector(state => state.course.loading);
  const totalItems = useAppSelector(state => state.course.totalItems);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        filter: filterState,
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: paginationState.sort,
      })
    );
  };

  const startSearching = e => {
    setFilterState(filterFloatingState);

    let _sort = paginationState.sort;
    if (customSortFloatingState) {
      _sort = customSortFloatingState;
    }

    setPaginationState({
      ...paginationState,
      activePage: 1,
      sort: _sort,
    });
    e.preventDefault();
  };

  const clear = () => {
    setFilterState('');
    setFilterFloatingState('');
    setCustomSortFloatingState('');
    setPaginationState({
      ...paginationState,
      activePage: 1,
      sort: 'id,asc'
    });
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?filter=${filterState}&page=${paginationState.activePage}&sort=${paginationState.sort}`;
    if (props.location.search !== endURL) {
      props.history.push(`${props.location.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [paginationState.activePage, paginationState.sort, filterState]);

  useEffect(() => {
    setFilterFloatingState(filterState);
    setCustomSortFloatingState(paginationState.sort);
  }, [paginationState.activePage, paginationState.sort]);

  useEffect(() => {
    const params = new URLSearchParams(props.location.search);
    const _page = params.get('page');
    const _sort = params.get(SORT);
    const _filter = params.get('filter');
    if (_page && _sort) {
      setPaginationState({
        ...paginationState,
        activePage: +_page,
        sort: _sort,
      });
    }
    if (_filter) {
      setFilterState(_filter);
    }
  }, [props.location.search]);

  const handlePagination = currentPage =>
    setPaginationState({
      ...paginationState,
      activePage: currentPage,
    });

  const handleSyncList = () => {
    sortEntities();
  };

  const {match} = props;

  return (
    <div>
      <h2 id="course-heading" data-cy="CourseHeading">
        Courses
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading}/> Refresh List
          </Button>
        </div>
      </h2>
      <Row>
        <Col sm="12">
          <Form onSubmit={startSearching}>
            <FormGroup>
              <InputGroup>
                <Input
                  type="text"
                  name="search"
                  value={filterFloatingState}
                  onChange={e => setFilterFloatingState(e.target.value)}
                  placeholder="Search for Course"
                />
                <Input
                  type="text"
                  name="order"
                  value={customSortFloatingState}
                  onChange={e => setCustomSortFloatingState(e.target.value)}
                  placeholder="Custom sort for Course"
                />
                <Button className="input-group-addon">
                  <FontAwesomeIcon icon="search"/>
                </Button>
                <Button type="reset" className="input-group-addon" onClick={clear}>
                  <FontAwesomeIcon icon="trash"/>
                </Button>
              </InputGroup>
            </FormGroup>
          </Form>
        </Col>
      </Row>
      <div className="table-responsive">
        {courseList && courseList.length > 0 ? (
          <Table responsive>
            <thead>
            <tr>
              <th className="hand">
                ID
              </th>
              <th className="hand">
                Title
              </th>
              <th className="hand">
                Image Url
              </th>
              <th>
                Teacher
              </th>
              <th/>
            </tr>
            </thead>
            <tbody>
            {courseList.map((course, i) => (
              <tr key={`entity-${i}`} data-cy="entityTable">
                <td>
                  <Button tag={Link} to={`${match.url}/${course.id}`} color="link" size="sm">
                    {course.id}
                  </Button>
                </td>
                <td>{course.title}</td>
                <td>{course.imageUrl}</td>
                <td>{course.teacher ? course.teacher.id : ''}</td>
                <td className="text-end">
                  <div className="btn-group flex-btn-group-container">
                    <Button tag={Link} to={`courses/${course.id}`} color="info" size="sm"
                            data-cy="entityDetailsButton">
                      <FontAwesomeIcon icon="eye"/> <span className="d-none d-md-inline">View</span>
                    </Button>
                  </div>
                </td>
              </tr>
            ))}
            </tbody>
          </Table>
        ) : (
          !loading && <div className="alert alert-warning">No Courses found</div>
        )}
      </div>
      {totalItems ? (
        <div className={courseList && courseList.length > 0 ? '' : 'd-none'}>
          <div className="justify-content-center d-flex">
            <JhiItemCount page={paginationState.activePage} total={totalItems}
                          itemsPerPage={paginationState.itemsPerPage}/>
          </div>
          <div className="justify-content-center d-flex">
            <JhiPagination
              activePage={paginationState.activePage}
              onSelect={handlePagination}
              maxButtons={5}
              itemsPerPage={paginationState.itemsPerPage}
              totalItems={totalItems}
            />
          </div>
        </div>
      ) : (
        ''
      )}
    </div>
  );
};

export default Explore;
