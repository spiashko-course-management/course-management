import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IModule } from 'app/shared/model/module.model';
import { getEntities as getModules } from 'app/entities/module/module.reducer';
import { getEntity, updateEntity, createEntity, reset } from './lesson.reducer';
import { ILesson } from 'app/shared/model/lesson.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { LessonType } from 'app/shared/model/enumerations/lesson-type.model';

export const LessonUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const modules = useAppSelector(state => state.module.entities);
  const lessonEntity = useAppSelector(state => state.lesson.entity);
  const loading = useAppSelector(state => state.lesson.loading);
  const updating = useAppSelector(state => state.lesson.updating);
  const updateSuccess = useAppSelector(state => state.lesson.updateSuccess);
  const lessonTypeValues = Object.keys(LessonType);
  const handleClose = () => {
    props.history.push('/entities/lesson');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(props.match.params.id));
    }

    dispatch(getModules({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...lessonEntity,
      ...values,
      module: modules.find(it => it.id.toString() === values.module.toString()),
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
          type: 'TEXT',
          ...lessonEntity,
          module: lessonEntity?.module?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="courseManagementApp.lesson.home.createOrEditLabel" data-cy="LessonCreateUpdateHeading">
            Create or edit a Lesson
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="lesson-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField
                label="Order"
                id="lesson-order"
                name="order"
                data-cy="order"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                  validate: v => isNumber(v) || 'This field should be a number.',
                }}
              />
              <ValidatedField
                label="Title"
                id="lesson-title"
                name="title"
                data-cy="title"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <ValidatedField label="Type" id="lesson-type" name="type" data-cy="type" type="select">
                {lessonTypeValues.map(lessonType => (
                  <option value={lessonType} key={lessonType}>
                    {lessonType}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField id="lesson-module" name="module" data-cy="module" label="Module" type="select" required>
                <option value="" key="0" />
                {modules
                  ? modules.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>This field is required.</FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/lesson" replace color="info">
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

export default LessonUpdate;
