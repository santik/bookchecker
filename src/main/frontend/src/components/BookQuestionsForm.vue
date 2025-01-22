<template>

  <form id="question-form" @submit.prevent="sendAnswers">
    <div v-for="question in questions" :key="question.id" class="form-group">
      <label class="control-label col-form-label fw-bold"
             v-bind:class="isCorrectAnswer(question.id)">{{ question.question }}</label>
      <br>
      <div v-for="answer in question.answers" :key="answer.id" class="form-check form-check-inline">
        <input class="form-check-input" type="radio" :id="`answer_${answer.id}`" :name="`userAnswers[${question.id}]`"
               :value="answer.id"
               v-model="userAnswers[question.id]">
        <label class="form-check-label" :for="`answer_${answer.id}`">
          {{ answer.answer }}
        </label>
      </div>
      <hr>
    </div>
    <SubmitButtonSpinner :isLoading="this.answersLoading" :buttonText="'Check answers'" v-if="this.submitAnswersShow"/>
    <RightWrong :isCorrect="this.isCorrect" v-if="this.answered"/>


  </form>

</template>

<script>

import SubmitButtonSpinner from '@/components/SubmitButtonSpinner.vue';
import RightWrong from '@/components/RightWrong.vue';
import {ref} from "vue";
import apiService from '@/services/apiService';


export default ({
  name: 'BookQuestionsForm',
  components: {
    SubmitButtonSpinner,
    RightWrong
  },
  props: {
    questions: {
      type: Array,
      required: true
    }
  },
  setup() {

    const userAnswers = ref({});
    var answersLoading = ref(false);
    var answered = ref(false);
    var isCorrect = ref(false);
    var userAnswersChecks = ref([]);
    var submitAnswersShow = ref(true);

    return {
      userAnswers,
      answersLoading,
      answered,
      isCorrect,
      userAnswersChecks,
      submitAnswersShow
    }

  },

  methods: {

    isCorrectAnswer(questionId) {
      // return questionId;
      if (!this.userAnswersChecks.length) {
        return '';
      }

      let targetAnswer = this.userAnswersChecks.find((userAnswerCheck) => userAnswerCheck.questionId === questionId);
      if (!targetAnswer) {
        return "";
      }
      return targetAnswer.correct ? 'text-success' : 'text-danger';
    },

    sendAnswers() {

      let requestAnswers = Object.entries(this.userAnswers).map(([questionId, answerId]) => ({
        questionId,
        answerId
      }));


      this.answersLoading = true;
      apiService.checkAnswers(requestAnswers)
          .then(response => {
            this.isCorrect = response.data.correct;
            this.userAnswersChecks = response.data.answers;
            this.answersLoading = false;
            this.answered = true;
            this.submitAnswersShow = false;
          })
          .catch(error => {
            console.log(error);
            this.answersLoading = false;
            if (error.response.status === 400) {
              this.answersLoading = false;
            } else if (error.response.status === 403) {
              this.$emit("forbidden", "")
              this.answersLoading = false;
            } else {
              this.submitAnswersShow = false;
            }
          });
    }
  }
});
</script>