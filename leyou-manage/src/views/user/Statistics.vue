<template>
  <div class="box">
    <h1>hello, {{name}}</h1>
  </div>
</template>

<script>
  export default {
    name: "statistics",
    data() {
      return {
        name: "社会我拓哥"
      }
    },
    methods:{

    }
  }
</script>

<style scoped>
  .box{
    background-color: blue;
    color: white;
  }
</style>
