import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './course.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const CourseDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity({
      id: props.match.params.id,
      include: '',
      filter: ''}));
  }, []);

  const courseEntity = useAppSelector(state => state.course.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="courseDetailsHeading">Course</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{courseEntity.id}</dd>
          <dt>
            <span id="title">Title</span>
          </dt>
          <dd>{courseEntity.title}</dd>
          <dt>
            <span id="imageUrl">Image Url</span>
          </dt>
          <dd>{courseEntity.imageUrl}</dd>
          <dt>Teacher</dt>
          <dd>{courseEntity.teacher ? courseEntity.teacher.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/entities/course" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/entities/course/${courseEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default CourseDetail;
