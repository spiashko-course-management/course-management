import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntities } from './completed-lesson.reducer';
import { ICompletedLesson } from 'app/shared/model/completed-lesson.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const CompletedLesson = (props: RouteComponentProps<{ url: string }>) => {
  const dispatch = useAppDispatch();

  const completedLessonList = useAppSelector(state => state.completedLesson.entities);
  const loading = useAppSelector(state => state.completedLesson.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  const handleSyncList = () => {
    dispatch(getEntities({}));
  };

  const { match } = props;

  return (
    <div>
      <h2 id="completed-lesson-heading" data-cy="CompletedLessonHeading">
        Completed Lessons
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Refresh List
          </Button>
          <Link to={`${match.url}/new`} className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp; Create new Completed Lesson
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {completedLessonList && completedLessonList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>ID</th>
                <th>Lesson</th>
                <th>Enrollment</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {completedLessonList.map((completedLesson, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`${match.url}/${completedLesson.id}`} color="link" size="sm">
                      {completedLesson.id}
                    </Button>
                  </td>
                  <td>
                    {completedLesson.lesson ? <Link to={`lesson/${completedLesson.lesson.id}`}>{completedLesson.lesson.id}</Link> : ''}
                  </td>
                  <td>
                    {completedLesson.enrollment ? (
                      <Link to={`enrollment/${completedLesson.enrollment.id}`}>{completedLesson.enrollment.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`${match.url}/${completedLesson.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`${match.url}/${completedLesson.id}/edit`}
                        color="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`${match.url}/${completedLesson.id}/delete`}
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" /> <span className="d-none d-md-inline">Delete</span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && <div className="alert alert-warning">No Completed Lessons found</div>
        )}
      </div>
    </div>
  );
};

export default CompletedLesson;
