import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, UncontrolledTooltip, Row, Col } from 'reactstrap';
import { byteSize } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './course-details.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const CourseDetailsDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const courseDetailsEntity = useAppSelector(state => state.courseDetails.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="courseDetailsDetailsHeading">CourseDetails</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{courseDetailsEntity.id}</dd>
          <dt>
            <span id="summary">Summary</span>
            <UncontrolledTooltip target="summary">basically md file</UncontrolledTooltip>
          </dt>
          <dd>{courseDetailsEntity.summary}</dd>
          <dt>Course</dt>
          <dd>{courseDetailsEntity.course ? courseDetailsEntity.course.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/course-details" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/course-details/${courseDetailsEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default CourseDetailsDetail;
