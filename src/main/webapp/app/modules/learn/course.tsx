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
import {getEntities} from "app/entities/course/course.reducer";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {JhiItemCount, JhiPagination} from "react-jhipster";

export const Course = (props: RouteComponentProps<{ url: string }>) => {


  return (
    <h2>
      Course ...
    </h2>
  );
};

export default Course;
