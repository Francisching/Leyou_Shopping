<template>
  <v-app :dark="dark">
    <!-- 左侧导航条 -->
    <v-navigation-drawer
      persistent
      :mini-variant="miniVariant"
      v-model="drawer"
      enable-resize-watcher
      fixed
      app
      width="200"
    >
      <v-toolbar flat class="transparent">
        <v-list class="pa-0">
          <v-list-tile avatar>
            <v-list-tile-avatar>
              <img src="../assets/2.jpeg">
            </v-list-tile-avatar>
            <v-list-tile-content>
              <v-list-tile-title>社会我拓哥</v-list-tile-title>
            </v-list-tile-content>
          </v-list-tile>
        </v-list>
      </v-toolbar>
      <v-divider/>
      <!-- 左侧菜单 -->
      <v-list class="pt-0" dense>
        <v-list-tile to="/index/dashboard">
          <v-list-tile-action>
            <v-icon>mdi-view-dashboard</v-icon>
          </v-list-tile-action>
          <v-list-tile-content>
            <v-list-tile-title>仪表盘</v-list-tile-title>
          </v-list-tile-content>

        </v-list-tile>
        <v-list-group
          v-model="item.active"
          v-for="item in items"
          :key="item.title"
          :prepend-icon="item.action"
          no-action
        >
          <!--一级菜单 -->
          <v-list-tile slot="activator">
            <v-list-tile-content>
              <v-list-tile-title>{{ item.title }}</v-list-tile-title>
            </v-list-tile-content>
          </v-list-tile>
          <!-- 二级菜单 -->
          <v-list-tile v-for="subItem in item.items" :key="subItem.title" :to="item.path + subItem.path">
            <v-list-tile-content>
              <v-list-tile-title>{{ subItem.title }}</v-list-tile-title>
            </v-list-tile-content>
            <v-list-tile-action>
              <v-icon>{{ subItem.action }}</v-icon>
            </v-list-tile-action>
          </v-list-tile>
        </v-list-group>
      </v-list>
    </v-navigation-drawer>
    <!-- 顶部工具条 -->
    <v-toolbar
      app
      dark
      color="primary"
    >
      <!-- 隐藏左侧菜单的按钮-->
      <v-toolbar-side-icon @click.stop="drawer = !drawer"/>
      <v-divider vertical></v-divider>
      <v-breadcrumbs :items="breads" divider=">" dark>
        <template v-slot:divider>
          <v-icon>mdi-chevron-double-right</v-icon>
        </template>
        <template v-slot:item="props">
            <span v-text="props.item.text" class="subheading"></span>
        </template>
      </v-breadcrumbs>

      <!-- 顶部导航标题 -->
      <v-flex xs3></v-flex>
      <v-toolbar-title v-text="title"/>
      <v-spacer/>
      <!-- 切换黑暗主题 -->
      <v-flex xs1>
        <v-switch
            :label="dark ? '暗黑' : '明亮'"
            v-model="dark"
            color="dark"
            hide-details
        />
      </v-flex>
      <!-- 调色板 -->
      <v-btn icon @click.stop="dark = !dark">
        <v-icon>invert_colors</v-icon>
      </v-btn>
      <!-- 顶部导航用户菜单 -->
      <v-btn icon @click.stop="dark = !dark">
        <v-icon>account_box</v-icon>
      </v-btn>
    </v-toolbar>
    <!--中间内容主体-->
    <v-content>

      <div>
        <!--定义一个路由锚点，Layout的子组件内容将在这里展示-->
        <router-view/>
      </div>
    </v-content>
  </v-app>
</template>

<script>
  import menus from "../config/menu";

  export default {
    data() {
      return {
        dark: false,// 是否暗黑主题
        drawer: true,// 左侧导航是否隐藏
        miniVariant: false,// 左侧导航是否收起
        title: '乐优商城后台管理',// 顶部导航条名称,
        menuMap: {}
      }
    },
    computed: {
      items() {
        return menus;
      },
      item1() {
        const arr = this.$route.path.split("/");
        return this.menuMap[arr[1]].name;
      },
      item2() {
        const arr = this.$route.path.split("/");
        return this.menuMap[arr[1]][arr[2]];
      },
      breads(){
        const arr = this.$route.path.split("/");
        return [this.menuMap["/" + arr[1]], this.menuMap[this.$route.path]];
      }
    },
    name: 'App',
    watch: {},
    created() {
      menus.forEach(({path:p1, title:t1, items}) => {
        this.menuMap[p1] = {text:t1, href: p1, disable: false};
        items.forEach(({title:t2, path: p2}) => {
          this.menuMap[p1 + p2] = {text:t2, href: p1 + p2, disable: false};
        })
      });
      this.menuMap['/index'] = {text: '首页', disable: false, href:'/index'};
      this.menuMap['/index/dashboard'] = {text: '仪表盘', disable: false, href:'/index/dashboard'};
    }
  }
</script>

<style scoped>
  .box {
    width: 90%;
  }
</style>
