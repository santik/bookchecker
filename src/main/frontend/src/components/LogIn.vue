<template>

  <div class="container">

    <form @submit.prevent="logIn">
      <div class="form-group">
        <label for="username" class="control-label col-form-label">Username</label>
        <input type="text" v-model="user.username" id="username" class="form-control"/>
      </div>
      <div class="form-group">
        <label for="password" class="control-label col-form-label">Password</label>
        <input type="password" v-model="user.password" id="password" class="form-control"/>
      </div>
      <br>
      <SubmitButtonSpinner :isLoading="loginLoading" :buttonText="'Log In'"/>
    </form>
  </div>

</template>


<script>
import apiService from '@/services/apiService';
import SubmitButtonSpinner from "@/components/SubmitButtonSpinner.vue";

export default {
  components: {SubmitButtonSpinner},
  data() {
    return {
      user: {
        username: '',
        password: '',
      },
      loginLoading: false
    }
  },
  methods: {
    async logIn() {
      const payload = {
        "username": this.user.username,
        "password": this.user.password
      };
      this.loginLoading = true;
      let logIn = await apiService.logIn(payload);
      this.loginLoading = false;
      console.log("Result login " + logIn);
      if (logIn === true) {
        this.$emit("login-success", "logged in")
      }
    }
  }
}
</script>

