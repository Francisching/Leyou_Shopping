<template>
  <v-card>
    <v-flex xs12 sm10>
      <v-tree url="/item/category/of/parent" isEdit
              @handleAdd="handleAdd"
              @handleEdit="handleEdit"
              @handleDelete="handleDelete"
      ></v-tree>
    </v-flex>
  </v-card>
</template>

<script>
  export default {
    name: "category",
    data() {
      return {
        isEdit: true
      }
    },
    methods: {
      handleAdd(node) {
        // 解析新增的节点数据，不要id
        const {id, ... param} = node;
        // 发起请求
        this.$http.post("/item/category", param)
          .then(({data}) => {
            // 回显id，因此返回值必须是id
            node.id = data;
          })
          .catch()
      },
      handleEdit(id, name) {
        this.$http.put("/item/category", this.$qs.stringify({id, name}))
          .then()
          .catch()
      },
      handleDelete(id) {
        this.$http.post("/item/category/" + id)
          .then()
          .catch()
      }
    }
  };
</script>

<style scoped>

</style>
