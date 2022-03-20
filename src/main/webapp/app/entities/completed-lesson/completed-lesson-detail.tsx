import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './completed-lesson.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const CompletedLessonDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const completedLessonEntity = useAppSelector(state => state.completedLesson.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="completedLessonDetailsHeading">CompletedLesson</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{completedLessonEntity.id}</dd>
          <dt>Lesson</dt>
          <dd>{completedLessonEntity.lesson ? completedLessonEntity.lesson.id : ''}</dd>
          <dt>Enrollment</dt>
          <dd>{completedLessonEntity.enrollment ? completedLessonEntity.enrollment.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/completed-lesson" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/completed-lesson/${completedLessonEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default CompletedLessonDetail;
