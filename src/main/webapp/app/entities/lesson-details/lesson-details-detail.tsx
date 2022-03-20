import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, UncontrolledTooltip, Row, Col } from 'reactstrap';
import { byteSize } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './lesson-details.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const LessonDetailsDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const lessonDetailsEntity = useAppSelector(state => state.lessonDetails.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="lessonDetailsDetailsHeading">LessonDetails</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{lessonDetailsEntity.id}</dd>
          <dt>
            <span id="content">Content</span>
            <UncontrolledTooltip target="content">basically md file with link to video if needed</UncontrolledTooltip>
          </dt>
          <dd>{lessonDetailsEntity.content}</dd>
          <dt>Lesson</dt>
          <dd>{lessonDetailsEntity.lesson ? lessonDetailsEntity.lesson.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/lesson-details" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/lesson-details/${lessonDetailsEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default LessonDetailsDetail;
