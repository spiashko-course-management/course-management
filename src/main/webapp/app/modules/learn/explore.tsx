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
import {
  Button,
  Card,
  CardBody,
  CardSubtitle,
  CardTitle,
  Col,
  Form,
  FormGroup,
  Input,
  InputGroup,
  Row
} from "reactstrap";
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

  const [paginationState, setPaginationState] = useState(
    overrideCmPaginationStateWithQueryParams(getCmSortState(props.location, ITEMS_PER_PAGE, ''), props.location.search)
  );

  const courseList = useAppSelector(state => state.course.entities);
  const loading = useAppSelector(state => state.course.loading);
  const totalItems = useAppSelector(state => state.course.totalItems);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        include: '(teacher)',
        filter: filterState ? `title=trgm=(${filterState},0.1)` : '',
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: filterState ? 'title,desc,trgm' : 'id,asc',
      })
    );
  };

  const startSearching = e => {
    setFilterState(filterFloatingState);

    setPaginationState({
      ...paginationState,
      activePage: 1,
    });
    e.preventDefault();
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?filter=${filterState}&page=${paginationState.activePage}`;
    if (props.location.search !== endURL) {
      props.history.push(`${props.location.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [paginationState.activePage, filterState]);

  useEffect(() => {
    setFilterFloatingState(filterState);
  }, [paginationState.activePage]);

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

  const {match} = props;

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="6">
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
                <Button className="input-group-addon">
                  <FontAwesomeIcon icon="search"/>
                </Button>
              </InputGroup>
            </FormGroup>
          </Form>
        </Col>
      </Row>
      <div>
        <Row className="justify-content-center">
          <Col md="7">
            {courseList && courseList.length > 0 ? (
                <Row>
                  {courseList.map((course, i) => (
                    <Col key={`entity-${i}`} md={4}>
                      <Card style={{width: '20rem', textDecoration: 'none'}} className="mb-4"
                            tag={Link} to={`courses/${course.id}`}
                            data-cy="courseDetailsCardLink"
                      >
                        <img
                          alt="Sample"
                          src={course.imageUrl}
                        />
                        <CardBody>
                          <CardTitle tag="h5" className="text-dark">
                            {course.title}
                          </CardTitle>
                          <CardSubtitle tag="h6" className="text-muted">
                            {'by ' + course.teacher.firstName + ' ' + course.teacher.lastName}
                          </CardSubtitle>
                        </CardBody>
                      </Card>
                    </Col>
                  ))}
                </Row>
              ) :
              (
                !loading && <div className="alert alert-warning">No Courses found</div>
              )}
          </Col>
        </Row>
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
