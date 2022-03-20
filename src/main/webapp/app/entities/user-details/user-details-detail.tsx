import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, UncontrolledTooltip, Row, Col } from 'reactstrap';
import { byteSize } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './user-details.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const UserDetailsDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const userDetailsEntity = useAppSelector(state => state.userDetails.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="userDetailsDetailsHeading">UserDetails</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{userDetailsEntity.id}</dd>
          <dt>
            <span id="bio">Bio</span>
            <UncontrolledTooltip target="bio">basically md file</UncontrolledTooltip>
          </dt>
          <dd>{userDetailsEntity.bio}</dd>
          <dt>User</dt>
          <dd>{userDetailsEntity.user ? userDetailsEntity.user.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/user-details" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/user-details/${userDetailsEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default UserDetailsDetail;
