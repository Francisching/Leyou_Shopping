<template>
  <v-app class="split-bg">
    <v-content>
      <v-container fluid fill-height>
        <v-layout align-center justify-center>
          <v-flex xs12 sm8 md4 lg4>
            <v-card class="elevation-1 pa-3">
              <v-card-text>
                <div class="layout column align-center">

                  <h1 class="flex my-4 primary--text">
                    乐优商城后台管理
                  </h1>
                </div>
                <v-form>
                  <v-text-field
                      append-icon="person"
                      name="login"
                      label="Login"
                      type="text"
                      v-model="model.username"
                      @keyup.enter="login"
                  ></v-text-field>
                  <v-text-field
                      append-icon="lock"
                      name="password"
                      label="Password"
                      id="password"
                      type="password"
                      v-model="model.password"
                      @keyup.enter="login"
                  ></v-text-field>
                </v-form>
              </v-card-text>
              <div class="login-btn">
                <v-btn icon>
                  <v-icon color="blue">fa fa-facebook-square fa-lg</v-icon>
                </v-btn>
                <v-btn icon>
                  <v-icon color="red">fa fa-google fa-lg</v-icon>
                </v-btn>
                <v-btn icon>
                  <v-icon color="light-blue">fa fa-twitter fa-lg</v-icon>
                </v-btn>
                <v-spacer></v-spacer>
                <v-btn block color="primary" @click="login" :loading="loading">Login</v-btn>
              </div>
            </v-card>
          </v-flex>
        </v-layout>
      </v-container>
    </v-content>
  </v-app>
</template>

<script>
  export default {
    data: () => ({
      loading: false,
      model: {
        username: "admin@itcast.cn",
        password: "admin"
      }
    }),
    methods: {
      login() {
        if (!this.model.username || !this.model.password) {
          this.loading = false;
          return false;
        }

        // 模拟登录
        this.loading = true;
        setTimeout(() => this.$router.push("/index/dashboard"), 400);
        /*this.$http.post("/auth/login", this.model)
          .then(resp => {
            this.loading = true;
            this.$router.push("/index/dashboard");
          })*/
      }
    }
  };
</script>
<style scoped lang="css">
  .split-bg {
    height: 50%;
    width: 100%;
    position: absolute;
    top: 0;
    left: 0;
    content: "";
    z-index: 0;
    background-color: #3F51B5;
  }
</style>