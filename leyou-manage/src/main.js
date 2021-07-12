import Vue from 'vue'
import './plugins/vuetify'
import App from './App.vue'
import router from './router'
import './config/http'
import MyComponent from './components/MyComponent'
import qs from 'qs'
import './assets/material.css';
import './assets/googleapis.css';
import './assets/material+icon.css';

Vue.prototype.$qs=qs;
Vue.use(MyComponent);
Vue.config.productionTip = false

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')
