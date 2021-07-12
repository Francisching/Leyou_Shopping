<template>
  <v-card class="mt-2">
    <v-toolbar class="elevation-0">

      <v-flex xs4>
        <v-btn color="primary" @click="addGoods">新增商品</v-btn>
      </v-flex>
      <v-flex xs2>
        <span class="subheading">状态：</span>
        <v-btn-toggle mandatory v-model.lazy="saleable">
          <v-btn large>
            全部
          </v-btn>
          <v-btn :value="true" large>
            上架
          </v-btn>
          <v-btn :value="false" large>
            下架
          </v-btn>
        </v-btn-toggle>
      </v-flex>

      <v-flex xs2>
        <v-cascader single box
                    url="/item/category/of/parent"
                    v-model="search.category"
                    label="商品分类"/>
      </v-flex>
      <v-spacer></v-spacer>
      <v-flex xs2>
        <!--品牌-->
        <v-autocomplete box
                        single-line
                        :items="brandOptions"
                        item-text="name"
                        item-value="id"
                        label="品牌"
                        v-model="search.brandId"
                        autocomplete
                        clearable
                        dense chips
        >
          <template slot="selection" slot-scope="data">
            <v-chip small>{{ data.item.name}}</v-chip>
          </template>
        </v-autocomplete>
      </v-flex>
      <v-spacer></v-spacer>
      <v-flex xs2>
        <v-text-field label="商品编号" class="mb-2" box @keyup.enter="refresh"
                      hide-details v-model="search.id" append-icon="search"
                      @click:append="refresh"
        ></v-text-field>
      </v-flex>

    </v-toolbar>
    <v-divider/>
    <v-data-table
        :headers="headers"
        :items="goodsList"
        :pagination.sync="pagination"
        :total-items="totalGoods"
        :loading="loading"
        class="elevation-1"
    >
      <template slot="items" slot-scope="props">
        <td class="text-xs-center">{{ props.item.id }}</td>
        <td class="text-xs-center">{{ props.item.name }}</td>
        <td class="text-xs-center">{{props.item.categoryName}}</td>
        <td class="text-xs-center">{{ props.item.brandName }}</td>
        <td class="justify-center layout px-0">
          <v-tooltip left>
            <v-btn slot="activator" icon @click="goodsDetail(props.item.id)">
              <i class="el-icon-search"/>
            </v-btn>
            <span> 查看商品</span>
          </v-tooltip>
          <v-tooltip left>
            <v-btn slot="activator" icon @click="editGoods(props.item)">
              <i class="el-icon-edit"/>
            </v-btn>
            <span> 修改商品</span>
          </v-tooltip>
          <v-tooltip left>
            <v-btn icon slot="activator" @click="deleteGoods(props.item)">
              <i class="el-icon-delete"/>
            </v-btn>
            <span> 删除商品</span>
          </v-tooltip>
          <v-tooltip left>
            <v-btn slot="activator" icon @click="updateSaleable(props.item.id, !props.item.saleable)">
              <i class="el-icon-upload2" v-if="!props.item.saleable"/>
              <i class="el-icon-download" v-else/>
            </v-btn>
            <span v-if="!props.item.saleable"> 上架商品</span>
            <span v-else> 下架商品</span>
          </v-tooltip>
        </td>
      </template>
    </v-data-table>
    <!--弹出的对话框-->
    <v-dialog max-width="800" v-model="show" persistent scrollable>
      <v-card>

        <!--对话框的内容，表单-->
        <v-card-text class="px-0 py-0" style="height: 550px;">
          <goods-form @fallback="previous" :oldGoods="oldGoods" :step="step" @close="closeWindow" :is-edit="isEdit" ref="goodsForm"/>
        </v-card-text>
        <!--底部按钮，用来操作步骤线-->
        <v-card-actions class="elevation-0">
          <v-flex class="xs3 mx-auto">
            <v-btn @click="previous" color="primary" :disabled="step === 1">上一步</v-btn>
            <v-btn @click="next" color="primary" :disabled="step === 4">下一步</v-btn>
          </v-flex>
          <v-flex xs1>
            <v-btn text icon>
              <v-icon @click="closeWindow">mdi-close-circle-outline</v-icon>
            </v-btn>
          </v-flex>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <v-dialog
        v-model="dialog"
        max-width="290"
    >
      <v-card>
        <h1>
          我是商品的预览页面
        </h1>
        <h1>
          我是商品的预览页面
        </h1>
        <h1>
          我是商品的预览页面
        </h1>
        <h1>
          我是商品的预览页面
        </h1>
      </v-card>


    </v-dialog>
  </v-card>
</template>

<script>
  // 导入自定义的表单组件
  import GoodsForm from './GoodsForm'

  export default {
    name: "goods",
    data() {
      return {
        dialog: false,
        saleable: true, // 上架还是下架
        brandOptions: [],
        search: {
          id: null,
          category: {},
          brandId: null
        }, // 搜索过滤字段
        totalGoods: 0, // 总条数
        goodsList: [], // 当前页品牌数据
        loading: true, // 是否在加载中
        pagination: {}, // 分页信息
        headers: [
          {text: 'id', align: 'center', sortable: false, value: 'id'},
          {text: '商品名称', align: 'center', sortable: false, value: 'name'},
          {text: '商品分类', align: 'center', sortable: false, value: 'categoryName'},
          {text: '品牌', align: 'center', value: 'brandName', sortable: false,},
          {text: '操作', align: 'center', sortable: false}
        ],
        show: false,// 控制对话框的显示
        oldGoods: {}, // 即将被编辑的商品信息
        isEdit: false, // 是否是编辑
        step: 1, // 子组件中的步骤线索引，默认为1
      }
    },
    mounted() { // 渲染后执行
      // 查询数据
      //this.getDataFromServer();
    },
    watch: {
      pagination: { // 监视pagination属性的变化
        deep: true, // deep为true，会监视pagination的属性及属性中的对象属性变化
        handler() {
          // 变化后的回调函数，这里我们再次调用getDataFromServer即可
          this.getDataFromServer();
        }
      },
      saleable() {
        this.refresh();
      },
      "search.category"(val) {
        if (val) {
          this.$http
            .get("/item/brand/of/category?id=" + val.id)
            .then(({data}) => {
              this.brandOptions = data;
            });
        }
        this.refresh();

      },
      "search.brandId"(val) {
        this.refresh();
      }
    },
    methods: {
      refresh() {
        if (this.pagination.page !== 1) {
          this.pagination.page = 1;
          return;
        }
        this.getDataFromServer();
      },
      getDataFromServer() { // 从服务的加载数的方法。
        // 发起请求
        this.$http.get("/item/goods/spu/page", {
          params: {
            id: this.search.id || null,
            categoryId: this.search.category ? this.search.category.id : null, // 搜索条件
            brandId: this.search.brandId || null,
            saleable: this.saleable === 0 ? null : this.saleable, // 上下架
            page: this.pagination.page,// 当前页
            rows: this.pagination.rowsPerPage,// 每页大小
          }
        }).then(resp => { // 这里使用箭头函数
          this.goodsList = resp.data.items;
          this.totalGoods = resp.data.total;
          // 完成赋值后，把加载状态赋值为false
          this.loading = false;
        }).catch(() => {
          this.goodsList = [];
          this.totalGoods = 0;
          // 完成赋值后，把加载状态赋值为false
          this.loading = false;
        })
      },
      addGoods() {
        // 修改标记
        this.isEdit = false;
        // 控制弹窗可见：
        this.show = true;
        // 把oldBrand变为null
        this.oldGoods = {};
      },
      deleteGoods(oldGoods) {
        if (oldGoods.saleable) {
          // 如果是上架商品，则不允许修改
          this.$message.error("不能修改上架商品，请先下架！");
          return;
        }
        this.$http.delete("/item/goods/" + oldGoods.id)
          .then(() => {

          })
          .catch(() => {

          })
      },
      async editGoods(oldGoods) {
        if (oldGoods.saleable) {
          // 如果是上架商品，则不允许修改
          this.$message.error("不能修改上架商品，请先下架！");
          return;
        }
        // 发起请求，查询spu、商品详情、和skus
        let goods = await this.$http.loadData("/item/goods/" + oldGoods.id);
        goods.categoryName = oldGoods.categoryName;
        goods.brandName = oldGoods.brandName;
        // 修改标记
        this.isEdit = true;
        // 控制弹窗可见：
        this.show = true;
        // 获取要编辑的goods
        this.oldGoods = goods;
      },
      closeWindow() {
        // 重新加载数据
        this.getDataFromServer();
        // 关闭窗口
        this.show = false;
        // 将步骤调整到1
        this.step = 1;
      },
      previous() {
        if (this.step > 1) {
          this.step--
        }
      },
      next() {
        if (this.step < 4 && this.$refs.goodsForm.$refs.basic.validate()) {
          this.step++
        }
      },
      updateSaleable(id, saleable) {
        this.$http.put("/item/goods/saleable", this.$qs.stringify({id, saleable}))
          .then(resp => {
            this.getDataFromServer();
            this.$message.success("操作成功！");
          })
          .catch(error => {
            this.$message.error("操作失败，请重试！");
          })
      },
      goodsDetail(id) {
        this.dialog = true
      }
    },
    components: {
      GoodsForm
    }
  }
</script>

<style scoped>

</style>
