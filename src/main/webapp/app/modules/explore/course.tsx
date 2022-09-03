import React, {useEffect} from 'react';
import {Link, RouteComponentProps} from 'react-router-dom';

import {
  Button,
  Card,
  CardBody, CardHeader,
  CardImg,
  CardSubtitle,
  CardText,
  CardTitle,
  Col,
  ListGroup,
  ListGroupItem,
  Row
} from 'reactstrap';

import {useAppDispatch, useAppSelector} from 'app/config/store';
import {getEntity} from "app/entities/course-extra-info/course-extra-info.reducer";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {LessonType} from "app/shared/model/enumerations/lesson-type.model";

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
  const loading = useAppSelector(state => state.courseExtraInfo.loading);

  const course = courseExtraInfoEntity.course
  const teacher = courseExtraInfoEntity.course?.teacher
  const modules = courseExtraInfoEntity.course?.modules
  const {match} = props;

  const loadedView = () => {
    return (
      <div>
        <Row className="justify-content-center">
          <Col md="6" className="text-center">
            <h2 data-cy="courseDetailsHeading">{course?.title}</h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="6" className="text-center">
            <Button tag={Link} to={`${match.url}`} replace color="primary" data-cy="entityDetailsBackButton">
              <span className="d-none d-md-inline">Start Course</span>
            </Button>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            <hr/>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="6">
            {/* <img */}
            {/*   alt="Sample" */}
            {/*   src={courseExtraInfoEntity.course?.imageUrl} */}
            {/*   className="float-left mr-2" */}
            {/* /> */}
            <p>{courseExtraInfoEntity.summary}</p>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="6" className="text-center">
            <h4>Course Curriculum</h4>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="6">
            {modules && modules.length > 0 ? (
                <div>
                  {modules.map((module, i) => (
                    <Card
                      className="my-2 p-0"
                      color="light"
                      key={`module-${i}`}
                    >
                      <CardHeader tag="h6" className="p-3">
                        {module.title}
                      </CardHeader>
                      <ListGroup>
                        {module.lessons && module.lessons.length > 0 ? (
                          <div>
                            {module.lessons.map((lesson, j) => (
                              <ListGroupItem key={`lesson-${i}-${j}`}>
                                {LessonType.VIDEO === lesson.type ? <FontAwesomeIcon icon="video"/> : ''}
                                {LessonType.TEXT === lesson.type ? <FontAwesomeIcon icon="file"/> : ''}
                                <span className="md-inline ms-2"> {lesson.title}</span>
                              </ListGroupItem>
                            ))}
                          </div>
                        ) : (
                          <div className="alert alert-warning">No Lessons</div>
                        )}
                      </ListGroup>
                    </Card>
                  ))}
                </div>
              )
              : (
                <div className="alert alert-warning">No Modules</div>
              )}
          </Col>
        </Row>
        <Row className="justify-content-center mt-5">
          <Col md="6" className="text-center">
            <h5>Authored by</h5>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="2" lg="2" xl="1">
            <Card className="text-decoration-none p-0"
                  tag={Link} to={`/explore/instructors/${teacher?.id}`}
                  data-cy="courseDetailsCardLink"
                  outline={true}
                  body={false}
                  color="light"
            >
              <CardImg
                src={teacher?.imageUrl || 'https://picsum.photos/200?x'}
                className="rounded-circle mb-3"
                top/>
              <CardTitle tag="h6" className="text-dark text-center">
                {teacher?.firstName + ' ' + teacher?.lastName}
              </CardTitle>
            </Card>
          </Col>
        </Row>
      </div>
    )
  }

  return (
    <div>
      {loading ? <p>Loading...</p> : (courseExtraInfoEntity.id ? loadedView() :
        <p>Info not found or you don&apos;t have access</p>)}
    </div>
  );
};

export default Course;
