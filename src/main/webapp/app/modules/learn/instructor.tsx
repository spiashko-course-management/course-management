import React, {useEffect, useState} from 'react';
import {Link, RouteComponentProps} from 'react-router-dom';

import {
  Button,
  Card,
  CardBody, CardHeader,
  CardImg,
  CardSubtitle,
  CardText,
  CardTitle,
  Col,
  ListGroup,
  ListGroupItem,
  Row
} from 'reactstrap';

import {useAppDispatch, useAppSelector} from 'app/config/store';
import {getEntity} from "app/entities/user-extra-info/user-extra-info.reducer";
import {getEntities as getCourseEntities} from "app/entities/course/course.reducer";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {LessonType} from "app/shared/model/enumerations/lesson-type.model";
import {getCmSortState, overrideCmPaginationStateWithQueryParams} from "app/shared/util/entity-utils";
import {ITEMS_PER_PAGE} from "app/shared/util/pagination.constants";
import {JhiItemCount, JhiPagination} from "react-jhipster";

export const Instructor = (props: RouteComponentProps<{ id: string }>) => {

  const dispatch = useAppDispatch();

  const [paginationState, setPaginationState] = useState(
    overrideCmPaginationStateWithQueryParams(getCmSortState(props.location, ITEMS_PER_PAGE, ''), props.location.search)
  );

  const userExtraInfoEntity = useAppSelector(state => state.userExtraInfo.entity);
  const loading = useAppSelector(state => state.courseExtraInfo.loading);

  const courseList = useAppSelector(state => state.course.entities);
  const courseListLoading = useAppSelector(state => state.course.loading);
  const totalItems = useAppSelector(state => state.course.totalItems);

  const user = userExtraInfoEntity.user

  useEffect(() => {
    dispatch(getEntity({
      id: props.match.params.id,
      include: '(user)',
      filter: ''
    }));
  }, []);

  useEffect(() => {
    if (!userExtraInfoEntity.id) {
      return;
    }
    dispatch(getCourseEntities({
      include: '',
      filter: `teacher.id==${userExtraInfoEntity.id}`,
      page: paginationState.activePage - 1,
      size: paginationState.itemsPerPage,
      sort: 'id,asc',
    }));
  }, [userExtraInfoEntity.id]);

  const handlePagination = currentPage =>
    setPaginationState({
      ...paginationState,
      activePage: currentPage,
    });

  const {match} = props;

  const loadedView = () => {
    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <Row>
              <Col md="3" lg="3" xl="2">
                <Card className="text-decoration-none p-0"
                      data-cy="courseDetailsCardLink"
                      outline={true}
                      body={false}
                      color="light"
                >
                  <CardImg
                    src={user?.imageUrl || 'https://picsum.photos/200?x'}
                    className="rounded-circle mb-3"
                    top/>
                  <CardTitle tag="h6" className="text-dark text-center">
                    {user?.firstName + ' ' + user?.lastName}
                  </CardTitle>
                </Card>
              </Col>
              <Col md="9">
                {userExtraInfoEntity?.bio}
              </Col>
            </Row>
          </Col>
        </Row>
        <Row className="justify-content-center my-5">
          <Col md="8" className="text-center">
            <h5>Courses by {user?.firstName}</h5>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {courseList && courseList.length > 0 ? (
                <Row>
                  {courseList.map((course, i) => (
                    <Col key={`entity-${i}`} md={4}>
                      <Card className="mb-4 text-decoration-none p-0"
                            tag={Link} to={`/courses/${course.id}`}
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
                        </CardBody>
                      </Card>
                    </Col>
                  ))}
                </Row>
              ) :
              (
                !courseListLoading && <div className="alert alert-warning">No Courses found</div>
              )}
          </Col>
        </Row>
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
    )
  }

  return (
    <div>
      {loading ? <p>Loading...</p> : (userExtraInfoEntity.id ? loadedView() :
        <p>Info not found or you don&apos;t have access</p>)}
    </div>
  );
};

export default Instructor;
