/*
 * Created on Wed Feb 10 2021
 *
 * The Unlicense
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or distribute
 * this software, either in source code form or as a compiled binary, for any
 * purpose, commercial or non-commercial, and by any means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors of this
 * software dedicate any and all copyright interest in the software to the public
 * domain. We make this dedication for the benefit of the public at large and to
 * the detriment of our heirs and successors. We intend this dedication to be an
 * overt act of relinquishment in perpetuity of all present and future rights to
 * this software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <https://unlicense.org>
 */

package org.seng302.example;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Repository interfaces are used to declare accessors to JPA objects.
 * 
 * Spring will scan the projet files for its own annotations and perform some
 * startup operations (e.g., instantiating classes).
 * 
 * By declaring a "repository rest resource", we can expose repository (JPA)
 * objects through REST API calls. However, This is discouraged in a
 * Model-View-Controller (or similar patterns).
 * 
 * See https://docs.spring.io/spring-data/rest/docs/current/reference/html/
 */
@RepositoryRestResource
interface StudentRepository extends JpaRepository<Student, Long> {

  /**
   * As Spring deal with the instantiation and injection of classes. it can
   * generate some code from method signatures using some introspection features.
   * 
   * In this case, you can create a "findBy" lastname method.
   * 
   * Note the @param annotation. This is linked to the "repository rest interface"
   * nature of this class. We need to tell that for the REST service that will be
   * created from this method, the REST calls are expecting a name parameters,
   * e.g., http://localhost:9499/students/search/findByLastName?lastname=Filipovic
   * 
   * @param name the name to search for
   * @return a (possibly empty) list of Student objects with their last names
   *         matching perfectly given name.
   */
  List<Student> findByLastName(@Param("lastname") String lastname);

}
