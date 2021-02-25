<template>
  <div>
    <h1 class="name">Seng302 Students</h1>
    <section class="studentapp">
      <div v-if="loading">
        <h1 class="loading">Loading...</h1>
      </div>
      <div v-else>
        <header class="header">
          <input
            class="new-student"
            autofocus
            autocomplete="off"
            :placeholder="this.inputPlaceholder"
            v-model="newStudent"
            @keyup.enter="addStudent"
          />
        </header>
        <section class="main" v-show="students.length" v-cloak>
          <input class="toggle-all" type="checkbox" v-model="allDone" />
          <ul class="student-list">
            <li
              v-for="student in filteredStudents"
              class="student"
              :key="student.id"
              :class="{
                checked: student.checked,
                editing: student == editedStudent,
              }"
            >
              <div class="view">
                <input
                  class="toggle"
                  type="checkbox"
                  v-model="student.checked"
                />
                <label @dblclick="editStudent(student)">{{
                  student.firstName.concat(" ").concat(student.lastName)
                }}</label>
                <button
                  class="destroy"
                  @click="removeStudent(student)"
                ></button>
              </div>
              <input
                class="edit"
                type="text"
                v-model="student.name"
                v-student-focus="student == editedStudent"
                @blur="doneEdit(student)"
                @keyup.enter="doneEdit(student)"
                @keyup.esc="cancelEdit(student)"
              />
            </li>
          </ul>
        </section>
        <footer class="footer" v-show="students.length" v-cloak>
          <span class="student-count">
            <strong>{{ remaining }}</strong> {{ remaining | pluralize }} left
          </span>
          <ul class="filters">
            <li>
              <a
                href="#/all"
                @click="setVisibility('all')"
                :class="{ selected: visibility == 'all' }"
                >All</a
              >
            </li>
            <li>
              <a
                href="#/checked"
                @click="setVisibility('checked')"
                :class="{ selected: visibility == 'checked' }"
                >Checked</a
              >
            </li>
          </ul>
          <button
            class="clear-checked"
            @click="removeChecked"
            v-show="students.length > remaining"
          >
            Delete checked
          </button>
        </footer>
      </div>
    </section>
    <div v-if="error" class="error" @click="handleErrorClick">
      ERROR: {{ this.error }}
    </div>
  </div>
</template>

<script>
import api from "../Api";

// visibility filters
let filters = {
  all: function (students) {
    return students;
  },
  active: function (students) {
    return students.filter(function (student) {
      return !student.checked;
    });
  },
  checked: function (students) {
    return students.filter(function (student) {
      return student.checked;
    });
  },
};

// app Vue instance
const Students = {
  name: "Students",

  // app initial state
  data: function () {
    return {
      students: [],
      newStudent: "",
      editedStudent: null,
      visibility: "all",
      loading: true,
      error: null,
    };
  },

  mounted() {
    api
      .getAll()
      .then((response) => {
        this.$log.debug("Data loaded: ", response.data);
        this.students = response.data;
      })
      .catch((error) => {
        this.$log.debug(error);
        this.error = "Failed to load students";
      })
      .finally(() => (this.loading = false));
  },

  // computed properties
  // http://vuejs.org/guide/computed.html
  computed: {
    filteredStudents: function () {
      return filters[this.visibility](this.students);
    },
    remaining: function () {
      return filters.active(this.students).length;
    },
    allDone: {
      get: function () {
        return this.remaining === 0;
      },
      set: function (value) {
        this.students.forEach(function (student) {
          student.checked = value;
        });
      },
    },
    inputPlaceholder: function () {
      return "Add a student?";
    },
  },

  filters: {
    pluralize: function (n) {
      return n === 1 ? "item" : "items";
    },
  },

  // methods that implement data logic.
  // note there's no DOM manipulation here at all.
  methods: {
    addStudent: function () {
      var value = this.newStudent && this.newStudent.trim();
      if (!value) {
        return;
      }
      // FIXME: no checks are done on validity of input (split may fail)
      // this is just for example purposes
      api
        .createNew(value.split(" ")[0], value.split(" ")[1], false)
        .then((response) => {
          this.$log.debug("New item created:", response);
          this.students.push({
            id: response.data.id,
            firstName: response.data.firstName,
            lastName: response.data.lastName,
            checked: false,
          });
        })
        .catch((error) => {
          this.$log.debug(error);
          this.error = "Failed to add student";
        });

      this.newStudent = "";
    },

    setVisibility: function (vis) {
      this.visibility = vis;
    },

    removeStudent: function (student) {
      api
        .removeForId(student.id)
        .then(() => {
          this.$log.debug("Item removed:", student);
          this.students.splice(this.students.indexOf(student), 1);
        })
        .catch((error) => {
          this.$log.debug(error);
          this.error = "Failed to remove student";
        });
    },

    editStudent: function (student) {
      this.beforeEditCache = student.name;
      this.editedStudent = student;
    },

    doneEdit: function (student) {
      if (!this.editedStudent) {
        return;
      }
      this.$log.info("Item updated:", student);
      // FIXME: no checks are done on validity of input (split may fail)
      // this is just for example purposes
      api
        .updateForId(
          student.id,
          student.name.split(" ")[0].trim(),
          student.name.split(" ")[1].trim()
        )
        .then((response) => {
          this.$log.info("Item updated:", response.data);
          this.editedStudent = null;

          // update element in this.students to reactively update view with new name
          var objIndex = this.students.findIndex((s) => s.id == student.id);
          this.students[objIndex].firstName = student.name.split(" ")[0].trim();
          this.students[objIndex].lastName = student.name.split(" ")[1].trim();
        })
        .catch((error) => {
          this.$log.debug(error);
          this.cancelEdit(student);
          this.error = "Failed to update student";
        });

      if (!student.name) {
        this.removeStudent(student);
      }
    },

    cancelEdit: function (student) {
      this.editedStudent = null;
      student.name = this.beforeEditCache;
    },

    removeChecked: function () {
      filters.checked(this.students).map(this.removeStudent);
    },

    handleErrorClick: function () {
      this.error = null;
    },
  },

  // a custom directive to wait for the DOM to be updated
  // before focusing on the input field.
  // http://vuejs.org/guide/custom-directive.html
  directives: {
    "student-focus": function (el, binding) {
      if (binding.value) {
        el.focus();
      }
    },
  },
};

export default Students;
</script>

<style>
[v-cloak] {
  display: none;
}
</style>