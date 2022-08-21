import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText, UncontrolledTooltip } from 'reactstrap';
import { isNumber, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { ILesson } from 'app/shared/model/lesson.model';
import { getEntities as getLessons } from 'app/entities/lesson/lesson.reducer';
import { getEntity, updateEntity, createEntity, reset } from './lesson-extra-info.reducer';
import { ILessonExtraInfo } from 'app/shared/model/lesson-extra-info.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const LessonExtraInfoUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const lessons = useAppSelector(state => state.lesson.entities);
  const lessonExtraInfoEntity = useAppSelector(state => state.lessonExtraInfo.entity);
  const loading = useAppSelector(state => state.lessonExtraInfo.loading);
  const updating = useAppSelector(state => state.lessonExtraInfo.updating);
  const updateSuccess = useAppSelector(state => state.lessonExtraInfo.updateSuccess);
  const handleClose = () => {
    props.history.push('/entities/lesson-extra-info');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(props.match.params.id));
    }

    dispatch(getLessons({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...lessonExtraInfoEntity,
      ...values,
      lesson: lessons.find(it => it.id.toString() === values.lesson.toString()),
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
          ...lessonExtraInfoEntity,
          lesson: lessonExtraInfoEntity?.lesson?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="courseManagementApp.lessonExtraInfo.home.createOrEditLabel" data-cy="LessonExtraInfoCreateUpdateHeading">
            Create or edit a LessonExtraInfo
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
                <ValidatedField name="id" required readOnly id="lesson-extra-info-id" label="ID" validate={{ required: true }} />
              ) : null}
              <ValidatedField
                label="Content"
                id="lesson-extra-info-content"
                name="content"
                data-cy="content"
                type="textarea"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <UncontrolledTooltip target="contentLabel">basically md file with link to video if needed</UncontrolledTooltip>
              <ValidatedField id="lesson-extra-info-lesson" name="lesson" data-cy="lesson" label="Lesson" type="select" required>
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/lesson-extra-info" replace color="info">
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

export default LessonExtraInfoUpdate;
