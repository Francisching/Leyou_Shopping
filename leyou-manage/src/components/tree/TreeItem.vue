<template>
  <div>
    <v-list-tile
        @click="toggle" class="level1 py-0 my-0" :class="{'selected':isSelected}" :dark="isSelected">
      <v-list-tile-avatar class="px-1">
        <v-icon v-if="model.isParent">{{open ? 'mdi-folder-open' : 'mdi-folder'}}</v-icon>
        <v-icon v-if="!model.isParent">mdi-file-outline</v-icon>
      </v-list-tile-avatar>
      <v-list-tile-content>
        <v-list-tile-title v-show="!beginEdit">
          <span>{{model.name}}</span>
        </v-list-tile-title>
        <input v-show="beginEdit" @click.stop="" :ref="model.id" v-model="model.name"
               @blur="afterEdit" @keyup.enter="afterEdit"/>
      </v-list-tile-content>
      <v-list-tile-action v-if="isEdit">
        <v-btn icon @mouseover="c1='primary'" @mouseout="c1=''" :color="c1" @click.stop="addChild">
          <i class="el-icon-plus"/>
        </v-btn>
      </v-list-tile-action>
      <v-list-tile-action v-if="isEdit">
        <v-btn icon @mouseover="c2='success'" @mouseout="c2=''" :color="c2" @click.stop="editChild">
          <i class="el-icon-edit"/>
        </v-btn>
      </v-list-tile-action>
      <v-list-tile-action v-if="isEdit">
        <v-btn icon @mouseover="c3='error'" @mouseout="c3=''" :color="c3" @click.stop="deleteChild">
          <i class="el-icon-delete"/>
        </v-btn>
      </v-list-tile-action>
    </v-list-tile>

    <v-list v-if="isFolder" v-show="open" class="ml-4 pt-0 pb-0" dense transition="scale-transition">
      <tree-item
          class="item"
          v-for="(model, index) in model.children"
          :key="index"
          :model="model"
          :url="url"
          :isEdit="isEdit"
          :nodes="nodes"
          :parentState="open"
          @handleAdd="handleAdd"
          @handleEdit="handleEdit"
          @handleDelete="handleDelete"
          @handleClick="handleClick"
      >
      </tree-item>
    </v-list>
  </div>
</template>

<script>
  import Vue from 'vue'

  export default {
    name: "tree-item",
    props: {
      model: Object,
      url: String,
      isEdit: {
        type: Boolean,
        default: false
      },
      nodes: Object,
      parentState: Boolean
    },
    mounted() {
      if (this.model.id === 0) {
        this.editChild();
      }
    },
    data: function () {
      return {
        c1: '',
        c2: '',
        c3: '',
        isSelected: false,
        open: false,
        beginEdit: false
      }
    },
    watch: {
      parentState(val) {
        if (!val) {
          this.open = val;
        }
      }
    },
    computed: {
      isFolder: function () {
        return this.model.children &&
          this.model.children.length > 0
      }
    },
    methods: {
      toggle: function () {
        // ?????????????????????????????????
        this.nodes.selected.isSelected = false;
        // ??????????????????
        this.isSelected = true;
        // ?????????????????????
        this.nodes.selected = this;

        // ?????????????????????????????????
        this.handleClick(this.model);

        // ???????????????????????????????????????????????????????????????
        if (this.model.parentId == 0) {
          // ?????????????????????????????????
          if (this.nodes.opened && this != this.nodes.opened) {
            // ?????????????????????????????????
            this.nodes.opened.open = false;
          }
          // ?????????????????????????????????
          this.nodes.opened = this;
        }
        // ??????????????????
        this.open = !this.open;
        // ???????????????????????????,??????????????????????????????????????????????????????????????????
        if (!this.model.isParent || this.isFolder || !this.open) {
          return;
        }
        // ????????????????????????
        this.$http.get(this.url, {params: {pid: this.model.id}})
          .then(resp => {
            Vue.set(this.model, 'children', resp.data);
            // ???????????????????????????
            this.model.children.forEach(n => {
              n['path'] = [];
              this.model.path.forEach(p => n['path'].push(p));
              n['path'].push(n.name);
            });
          }).catch(e => {
          console.log(e);
        });
      },
      addChild: function () {
        let child = {
          id: 0,
          name: '????????????',
          parentId: this.model.id,
          isParent: false,
          sort: this.model.children ? this.model.children.length + 1 : 1
        };
        if (!this.model.isParent) {
          Vue.set(this.model, 'children', [child]);
          this.model.isParent = true;
          this.open = true;
          this.handleAdd(child);
        } else {
          if (!this.isFolder) {
            this.$http.get(this.url, {params: {pid: this.model.id}}).then(resp => {
              Vue.set(this.model, 'children', resp.data);
              this.model.children.push(child);
              this.open = true;
              // this.handleAdd(child);
            });
          } else {
            this.model.children.push(child);
            this.open = true;
            // this.handleAdd(child);
          }
        }
      },
      deleteChild: function () {
        this.$message.confirm('??????????????????????????????????????????????', '??????', {
          confirmButtonText: '????????????',
          cancelButtonText: '??????',
          type: 'warning'
        }).then(() => {
          this.handleDelete(this.model.id);
        }).catch(() => {
          this.$message.info('???????????????');
        })

      },
      editChild() {
        this.beginEdit = true;
        this.$nextTick(() => this.$refs[this.model.id].focus());
      },
      afterEdit() {
        if (this.beginEdit) {
          this.beginEdit = false;
          if (this.model.id === 0) {
            this.handleAdd(this.model);
          } else {
            this.handleEdit(this.model.id, this.model.name);
          }
        }
      },
      handleAdd(node) {
        this.$emit("handleAdd", node);
      },
      handleDelete(id) {
        this.$emit("handleDelete", id);
      },
      handleEdit(id, name) {
        this.$emit("handleEdit", id, name)
      },
      handleClick(node) {
        this.$emit("handleClick", node);
      }
    }
  }
</script>

<style scoped>
  .level1 {
    height: 40px;
  }

  .selected {
    background-color: #3F51B5;
    color: white;
  }

  .material-icons {
    line-height: 1.7
  }
</style>
