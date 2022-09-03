import React, {useEffect, useState} from 'react';
import {Link, RouteComponentProps} from 'react-router-dom';

import {useAppDispatch, useAppSelector} from "app/config/store";
import {
  getCmSortState,
  getFromQueryParams,
  overrideCmPaginationStateWithQueryParams
} from "app/shared/util/entity-utils";
import {ITEMS_PER_PAGE, ITEMS_PER_PAGE_SMALL, SORT} from "app/shared/util/pagination.constants";
import {getEntities} from "app/entities/enrollment/enrollment.reducer";
import {
  Button,
  Card,
  CardBody, CardImg,
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

export const Enrollments = (props: RouteComponentProps<{ url: string }>) => {

  const dispatch = useAppDispatch();

  const [filterState, setFilterState] = useState(
    getFromQueryParams(props.location.search, 'filter')
  );
  const [filterFloatingState, setFilterFloatingState] = useState(
    getFromQueryParams(props.location.search, 'filter')
  );

  const [paginationState, setPaginationState] = useState(
    overrideCmPaginationStateWithQueryParams(getCmSortState(props.location, ITEMS_PER_PAGE_SMALL, ''), props.location.search)
  );

  const enrollments = useAppSelector(state => state.enrollment.entities);
  const courseList = enrollments.map(e => e.course);
  const account = useAppSelector(state => state.authentication.account);
  const loading = useAppSelector(state => state.enrollment.loading);
  const totalItems = useAppSelector(state => state.enrollment.totalItems);

  const getAllEntities = () => {
    if (!account.id) {
      return;
    }
    dispatch(
      getEntities({
        include: '(course(teacher))',
        filter: `student.id==${account.id}` + (filterState ? `&&course.title=trgm=(${filterState},0.1)` : ''),
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: 'id,asc',
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
  }, [paginationState.activePage, filterState, account.id]);

  useEffect(() => {
    setFilterFloatingState(filterState);
  }, [paginationState.activePage]);


  useEffect(() => {
    const params = new URLSearchParams(props.location.search);
    const _page = params.get('page');
    const _sort = params.get(SORT);
    if (_page && _sort) {
      setPaginationState({
        ...paginationState,
        activePage: +_page,
        sort: _sort,
      });
    }
  }, [props.location.search]);

  const handlePagination = currentPage =>
    setPaginationState({
      ...paginationState,
      activePage: currentPage,
    });

  const {match} = props;

  const loadedView = () => {
    return <div>
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
                  placeholder="Search for enrollment"
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
                      <Card className="mb-4 text-decoration-none p-0"
                            tag={Link} to={`/learn/courses/${course.id}`}
                            data-cy="courseDetailsCardLink"
                      >
                        <CardImg
                          src={course.imageUrl}
                          top
                        />
                        <CardBody>
                          <CardTitle tag="h5" className="text-dark">
                            {course.title}
                          </CardTitle>
                          <CardSubtitle tag="h6" className="text-muted">
                            {'by ' + course.teacher?.firstName + ' ' + course.teacher?.lastName}
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
  };

  return (
    <div>
      {!account.id && loading ? <p>Loading...</p> : (courseList && courseList.length > 0 ? loadedView() :
        <p>Info not found or you don&apos;t have access</p>)}
    </div>
  );
};

export default Enrollments;
