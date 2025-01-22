<template>
  <div class="p-2 mb-2 bg-success text-white w-25 rounded text-center"  v-if="isCorrect">Correct!</div>
  <div class="p-2 mb-2 bg-danger text-white w-25 rounded text-center" v-if="!isCorrect">Wrong! Get new questions</div>
</template>

<script>
export default {
  props: {
    isCorrect: {
      type: Boolean,
      required: true,
    },
  },
};
</script>
