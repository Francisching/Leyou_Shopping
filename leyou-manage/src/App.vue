<template>
  <router-view></router-view>
</template>

<script>

  export default {
    data() {
      return {
        name: "社会我拓哥",
        num: 10
      }
    },
    methods:{
      add(){
        this.num++;
      }
    }
  }
</script>

<style scoped>
  .btn{
    width: 100px;
    height: 30px;
    border: 1px solid #000;
    border-radius: 5px;
  }
  .btn:hover{
    border: 1px solid deepskyblue;
    color: deepskyblue;

  }
</style>
