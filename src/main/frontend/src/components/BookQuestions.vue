<template>

  <div class="container">

    <h3>Check the book</h3>
    <form @submit.prevent="getQuestions">
      <div class="form-group">
        <label for="bookName" class="control-label col-form-label">Book Name</label>
        <input type="text" v-model="book.bookName" id="bookName" class="form-control"/>
      </div>
      <div class="form-group">
        <label for="chapter" class="control-label col-form-label">Chapter Number</label>
        <input type="number" v-model="book.chapter" id="chapter" class="form-control"/>
      </div>
      <div class="form-group">
        <label for="language" class="control-label col-form-label ">Language</label>
        <select  v-model="book.language" id="language" class="form-control">
          <option value="english">English</option>
          <option value="dutch">Nederlands</option>
          <option value="russian">Russian</option>
        </select>
      </div>
      <br>
      <SubmitButtonSpinner :isLoading="questionsLoading" :buttonText="'Get Questions'"/>
    </form>

    <hr>

    <BookQuestionsForm :questions="questions" v-if="showBookQuestionsForm" @forbidden="handleForbidden"/>

  </div>

</template>


<script>
import BookQuestionsForm from "@/components/BookQuestionsForm.vue";
import SubmitButtonSpinner from '@/components/SubmitButtonSpinner.vue';
import apiService from '@/services/apiService';


export default {
  components: {
    BookQuestionsForm,
    SubmitButtonSpinner
  },
  data() {
    return {
      book: {
        bookName: 'Harry potter and philosopher\'s stone',
        chapter: '1',
        language: "english"
      },
      questions: {},
      showBookQuestionsForm: false,
      questionsLoading: false
    }
  },
  methods: {
    getQuestions() {
      const payload = {
        "bookName": this.book.bookName,
        "chapter": this.book.chapter,
        "language": this.book.language
      };

      this.questionsLoading = true;
      this.showBookQuestionsForm = false;
      apiService.getQuestions(payload)
          .then(response => {
            this.questions = response.data.questions;
            this.showBookQuestionsForm = true;
            this.questionsLoading = false;
          })
          .catch(error => {
            console.log(error);
            if (error.response.status === 403) {
              this.$emit("forbidden", "")
            }
            this.questionsLoading = false;
          });
    },
    handleForbidden() {
      this.$emit("forbidden", "")
    },
  }
}
</script>

