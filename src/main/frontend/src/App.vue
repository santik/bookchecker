<template>
  <LogIn v-if="!isLoggedIn" @login-success="handleLogin"/>
  <BookQuestions v-if="isLoggedIn" @forbidden="handleForbidden"/>
</template>

<script>
import BookQuestions from './components/BookQuestions.vue'
import logIn from "./components/LogIn.vue";

export default {
  name: 'App',
  components: {
    LogIn: logIn,
    BookQuestions: BookQuestions
  },
  data() {
    return {
      isLoggedIn: false,
    };
  },
  methods: {
    handleLogin() {
      this.isLoggedIn = true;
    },
    handleForbidden() {
      this.isLoggedIn = false;
    },
  },
}
</script>
