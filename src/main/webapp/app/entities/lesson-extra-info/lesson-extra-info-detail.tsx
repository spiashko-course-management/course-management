import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, UncontrolledTooltip, Row, Col } from 'reactstrap';
import { byteSize } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './lesson-extra-info.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const LessonExtraInfoDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const lessonExtraInfoEntity = useAppSelector(state => state.lessonExtraInfo.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="lessonExtraInfoDetailsHeading">LessonExtraInfo</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{lessonExtraInfoEntity.id}</dd>
          <dt>
            <span id="content">Content</span>
            <UncontrolledTooltip target="content">basically md file with link to video if needed</UncontrolledTooltip>
          </dt>
          <dd>{lessonExtraInfoEntity.content}</dd>
          <dt>Lesson</dt>
          <dd>{lessonExtraInfoEntity.lesson ? lessonExtraInfoEntity.lesson.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/entities/lesson-extra-info" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/entities/lesson-extra-info/${lessonExtraInfoEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default LessonExtraInfoDetail;
