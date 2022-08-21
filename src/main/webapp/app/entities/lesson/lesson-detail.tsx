import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './lesson.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const LessonDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const lessonEntity = useAppSelector(state => state.lesson.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="lessonDetailsHeading">Lesson</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{lessonEntity.id}</dd>
          <dt>
            <span id="order">Order</span>
          </dt>
          <dd>{lessonEntity.order}</dd>
          <dt>
            <span id="title">Title</span>
          </dt>
          <dd>{lessonEntity.title}</dd>
          <dt>
            <span id="type">Type</span>
          </dt>
          <dd>{lessonEntity.type}</dd>
          <dt>Module</dt>
          <dd>{lessonEntity.module ? lessonEntity.module.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/entities/lesson" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/entities/lesson/${lessonEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default LessonDetail;
