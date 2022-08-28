import React, {useEffect, useState} from 'react';
import {Link, RouteComponentProps} from 'react-router-dom';

import {Row, Col, Button, Form, FormGroup, InputGroup, Input, Table} from 'reactstrap';

import {useAppDispatch, useAppSelector} from 'app/config/store';
import {
  getCmSortState,
  getFromQueryParams,
  overrideCmPaginationStateWithQueryParams
} from "app/shared/util/entity-utils";
import {ITEMS_PER_PAGE, SORT} from "app/shared/util/pagination.constants";
import {getEntities, getEntity} from "app/entities/course-extra-info/course-extra-info.reducer";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {JhiItemCount, JhiPagination} from "react-jhipster";

export const Course = (props: RouteComponentProps<{ id: string }>) => {

  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity({
      id: props.match.params.id,
      include: '(course(modules(lessons),teacher))',
      filter: ''
    }));
  }, []);

  const courseExtraInfoEntity = useAppSelector(state => state.courseExtraInfo.entity);

  const {match} = props;

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8" className="text-center">
          <h2 data-cy="courseDetailsHeading">{courseExtraInfoEntity.course?.title}</h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8" className="text-center">
          <Button tag={Link} to={`${match.url}`} replace color="primary" data-cy="entityDetailsBackButton">
            <span className="d-none d-md-inline">Start Course</span>
          </Button>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="10">
          <hr/>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8" className="justify-content-center">
          <h2 data-cy="courseDetailsHeading">{courseExtraInfoEntity.summary}</h2>
        </Col>
      </Row>
    </div>
  );
};

export default Course;
