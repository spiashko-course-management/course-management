import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { ILesson } from 'app/shared/model/lesson.model';
import { getEntities as getLessons } from 'app/entities/lesson/lesson.reducer';
import { IEnrollment } from 'app/shared/model/enrollment.model';
import { getEntities as getEnrollments } from 'app/entities/enrollment/enrollment.reducer';
import { getEntity, updateEntity, createEntity, reset } from './completed-lesson.reducer';
import { ICompletedLesson } from 'app/shared/model/completed-lesson.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const CompletedLessonUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const lessons = useAppSelector(state => state.lesson.entities);
  const enrollments = useAppSelector(state => state.enrollment.entities);
  const completedLessonEntity = useAppSelector(state => state.completedLesson.entity);
  const loading = useAppSelector(state => state.completedLesson.loading);
  const updating = useAppSelector(state => state.completedLesson.updating);
  const updateSuccess = useAppSelector(state => state.completedLesson.updateSuccess);
  const handleClose = () => {
    props.history.push('/entities/completed-lesson');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(props.match.params.id));
    }

    dispatch(getLessons({}));
    dispatch(getEnrollments({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...completedLessonEntity,
      ...values,
      lesson: lessons.find(it => it.id.toString() === values.lesson.toString()),
      enrollment: enrollments.find(it => it.id.toString() === values.enrollment.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...completedLessonEntity,
          lesson: completedLessonEntity?.lesson?.id,
          enrollment: completedLessonEntity?.enrollment?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="courseManagementApp.completedLesson.home.createOrEditLabel" data-cy="CompletedLessonCreateUpdateHeading">
            Create or edit a CompletedLesson
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField name="id" required readOnly id="completed-lesson-id" label="ID" validate={{ required: true }} />
              ) : null}
              <ValidatedField id="completed-lesson-lesson" name="lesson" data-cy="lesson" label="Lesson" type="select" required>
                <option value="" key="0" />
                {lessons
                  ? lessons.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>This field is required.</FormText>
              <ValidatedField
                id="completed-lesson-enrollment"
                name="enrollment"
                data-cy="enrollment"
                label="Enrollment"
                type="select"
                required
              >
                <option value="" key="0" />
                {enrollments
                  ? enrollments.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>This field is required.</FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/completed-lesson" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">Back</span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp; Save
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default CompletedLessonUpdate;
