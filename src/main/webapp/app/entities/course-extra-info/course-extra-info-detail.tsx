import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, UncontrolledTooltip, Row, Col } from 'reactstrap';
import { byteSize } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './course-extra-info.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const CourseExtraInfoDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const courseExtraInfoEntity = useAppSelector(state => state.courseExtraInfo.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="courseExtraInfoDetailsHeading">CourseExtraInfo</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{courseExtraInfoEntity.id}</dd>
          <dt>
            <span id="summary">Summary</span>
            <UncontrolledTooltip target="summary">basically md file</UncontrolledTooltip>
          </dt>
          <dd>{courseExtraInfoEntity.summary}</dd>
          <dt>Course</dt>
          <dd>{courseExtraInfoEntity.course ? courseExtraInfoEntity.course.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/entities/course-extra-info" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/entities/course-extra-info/${courseExtraInfoEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default CourseExtraInfoDetail;
