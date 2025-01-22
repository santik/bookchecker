<template>
  <button type="submit" class="btn btn-primary" :disabled="isLoading">
  <span v-if="isLoading">
  <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
  Loading...
  </span>
  <span v-if="!isLoading">{{buttonText}}</span>
  </button>
</template>

<script>
export default {
  props: {
    isLoading: {
      type: Boolean,
      required: true,
    },
    buttonText: {
      type: String,
      default: 'Submit'
    }
  },
};
</script>
