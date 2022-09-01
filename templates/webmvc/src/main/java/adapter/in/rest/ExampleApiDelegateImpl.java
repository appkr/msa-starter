package {{packageName}}.adapter.in.rest;

import {{packageName}}.adapter.in.rest.error.BadRequestAlertException;
import {{packageName}}.adapter.in.rest.mapper.ExampleMapper;
import {{packageName}}.application.ExampleService;
import {{packageName}}.domain.Example;
import {{packageName}}.rest.ExampleApiDelegate;
import {{packageName}}.rest.ExampleDto;
import {{packageName}}.rest.ExampleListDto;
import {{packageName}}.rest.PageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.NoSuchElementException;

import static {{packageName}}.adapter.in.rest.error.ErrorConstants.CONSTRAINT_VIOLATION_TYPE;

/**
 * Given:
 *  UAA is working at port 9999
 *
 *  When: Login to UAA and save access_token to ACCESS_TOKEN variable
 *  ```bash
 *  $ RESPONSE=$(curl -s -X POST --data "username=user&password=user&grant_type=password&scope=openid" http://web_app:changeit@localhost:9999/oauth/token)
 *  $ ACCESS_TOKEN=$(echo $RESPONSE | jq .access_token | xargs)
 *
 * Then: Use API resources with the given ACCESS_TOKEN
 *  ```bash
 *  $ curl -s -H "Authorization: bearer ${ACCESS_TOKEN}" http://localhost:{{portNumber}}/api/examples
 *  {
 *    "data": [
 *      {
 *        "exampleId": 1,
 *        "title": "original title",
 *        "createdAt": "2020-12-07T20:54:50.905309+09:00",
 *        "updatedAt": "2020-12-07T20:54:50.905339+09:00",
 *        "createdBy": "user",
 *        "updatedBy": "user"
 *      }
 *    ],
 *    "page": {
 *      "size": 1,
 *      "totalElements": 1,
 *      "totalPages": 1,
 *      "number": 1
 *    }
 *  }
 *  ```
 */
@Component
@RequiredArgsConstructor
public class ExampleApiDelegateImpl implements ExampleApiDelegate {

  private final ExampleService service;
  private final ExampleMapper mapper;

  @Override
  public ResponseEntity<ExampleListDto> listExamples(Integer page, Integer size) {
    final Page<Example> examplePage = service.listExamples(PageRequest.of(page - 1, size));

    final List<ExampleDto> dtoList = mapper.toDto(examplePage.getContent());
    final PageDto pageDto = new PageDto()
        .number(examplePage.getNumber() + 1)
        .size(examplePage.getSize())
        .totalPages(examplePage.getTotalPages())
        .totalElements(examplePage.getTotalElements());

    return ResponseEntity.ok(new ExampleListDto().data(dtoList).page(pageDto));
  }

  /**
   * When: Authorization header is not given
   * ```
   * $ curl -s http://localhost:{{portNumber}}/api/exceptions
   * {
   *   "type": "{{projectName}}/problem-with-message",
   *   "title": "Unauthorized",
   *   "status": 401,
   *   "detail": "Full authentication is required to access this resource",
   *   "path": "/api/exceptions",
   *   "message": "error.http.401"
   * }
   * ```
   *
   * When: type=Unacceptable is given
   * ```
   * $ curl -s -H "Authorization: bearer ${ACCESS_TOKEN}" http://localhost:{{portNumber}}/api/exceptions?type=Unacceptable
   * {
   *   "errorKey": "{{projectName}}/constraint-violation",
   *   "type": "example/problem-with-message",
   *   "title": "Unacceptable type given: Unacceptable",
   *   "status": 400,
   *   "message": "error.example/constraint-violation",
   *   "params": null
   * }
   * ```
   *
   * When: type=ServerError is given
   * ```
   * $ curl -s -H "Authorization: bearer ${ACCESS_TOKEN}" http://localhost:{{portNumber}}/api/exceptions?type=ServerError
   * {
   *   "type": "{{projectName}}/problem-with-message",
   *   "title": "Internal Server Error",
   *   "status": 500,
   *   "detail": "RuntimeException",
   *   "path": "/api/exceptions",
   *   "message": "error.http.500"
   * }
   * ```
   *
   * When: type=NoSuchElement is given
   * ```
   * $ curl -s -H "Authorization: bearer ${ACCESS_TOKEN}" http://localhost:{{portNumber}}/api/exceptions?type=NoSuchElement
   * {
   *   "type": "{{projectName}}/problem-with-message",
   *   "status": 404,
   *   "path": "/api/exceptions",
   *   "message": "NoSuchElementException"
   * }
   *
   * When: Not existing endpoint is given
   * ```
   * $ curl -s -H "Authorization: bearer ${ACCESS_TOKEN}" http://localhost:{{portNumber}}/api/not-existing
   * {
   *   "type": "{{projectName}}/problem-with-message",
   *   "title": "Not Found",
   *   "status": 404,
   *   "detail": "No handler found for GET /api/not-existing",
   *   "path": "/api/not-existing",
   *   "message": "error.http.404"
   * }
   * ```
   */
  @Override
  public ResponseEntity<Void> getException(String type) {
    switch (type) {
      case "ServerError":
        throw new RuntimeException("RuntimeException");
      case "NoSuchElement":
        throw new NoSuchElementException("NoSuchElementException");
      default:
        throw new BadRequestAlertException("Unacceptable type given: " + type,
            null, CONSTRAINT_VIOLATION_TYPE.toString());
    }
  }
}
