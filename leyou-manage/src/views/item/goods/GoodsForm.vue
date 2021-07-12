<template>
  <v-stepper v-model="step" class="elevation-0">
    <v-stepper-header>
      <v-stepper-step :complete="step > 1" step="1">基本信息</v-stepper-step>
      <v-divider/>
      <v-stepper-step :complete="step > 2" step="2">商品描述</v-stepper-step>
      <v-divider/>
      <v-stepper-step :complete="step > 3" step="3">规格参数</v-stepper-step>
      <v-divider/>
      <v-stepper-step step="4">SKU属性</v-stepper-step>
    </v-stepper-header>
    <v-stepper-items>
      <!--1、基本信息-->
      <v-stepper-content step="1">
        <v-flex class="xs10 mx-auto">
          <v-form v-model="valid" ref="basic">
            <!--商品分类-->
            <v-cascader
                url="/item/category/of/parent"
                required
                showAllLevels
                v-model="goods.categories"
                label="请选择商品分类"/>
            <!--品牌-->
            <v-autocomplete
                :items="brandOptions"
                item-text="name"
                item-value="id"
                label="所属品牌"
                v-model="goods.brandId"
                required
                autocomplete
                clearable
                dense chips
                :rules="[v => !!v || '品牌不能为空']"
            >
              <template slot="selection" slot-scope="data">
                <v-chip small>{{ data.item.name}}</v-chip>
              </template>
            </v-autocomplete>
            <v-text-field label="商品名称" v-model="goods.name" :counter="200" required :rules="[v => !!v || '商品标题不能为空']"/>
            <v-textarea label="商品标题(会影响到搜索排名)" v-model="goods.title" :rows="4" :counter="200"/>
          </v-form>
        </v-flex>
      </v-stepper-content>
      <!--2、商品描述-->
      <v-stepper-content step="2">
        <v-textarea label="包装清单" v-model="goods.spuDetail.packingList" :counter="1000" :rows="3" hide-details/>
        <v-textarea label="售后服务" v-model="goods.spuDetail.afterService" :counter="1000" :rows="3" hide-details
                    class="mb-4"/>
        <span class="subheading font-weight-thin">商品描述</span>
        <v-editor v-model="goods.spuDetail.description" url="/auth/ali/oss/signature" needSignature class="mt-3"/>
      </v-stepper-content>
      <!--3、规格参数-->
      <v-stepper-content step="3">
        <v-card>
          <v-flex xs3 class="headline mx-auto">通用规格属性</v-flex>
          <v-card-text>
            <v-container grid-list-md fluid class="mt-0">

              <v-layout wrap row justify-space-between class="px-4">
                <v-flex xs12 sm5 v-for="param in specs" :key="param.name">
                  <v-select :label="param.name" :items="param.ops" v-model="param.v"
                            v-if="param.ops && param.ops.length > 0"></v-select>
                  <v-text-field v-else :label="param.name" v-model="param.v" :suffix="param.unit || ''"
                  />
                </v-flex>
              </v-layout>
            </v-container>
          </v-card-text>
        </v-card>
        <!--遍历特有规格参数-->
        <v-divider></v-divider>
        <v-card>
          <v-flex xs3 class="headline mx-auto">特有用规格属性</v-flex>
          <v-card-text>
            <v-card flat v-for="spec in specialSpecs" :key="spec.name">
              <!--特有参数的标题-->
              <v-flex class="subheading">{{spec.name}}:</v-flex>
              <!--特有参数的待选项，需要判断是否有options，如果没有，展示文本框，让用户自己输入-->
              <v-flex>
                <v-card-text class="px-4">
                  <v-container grid-list-md fluid class="mt-0" v-if="spec.ops && spec.ops.length > 0">
                    <v-layout wrap row justify-space-between class="px-4">
                      <v-flex xs12 sm3 v-for="(o,i) in spec.ops" :key="o">
                        <v-checkbox :label="o" :value="o" v-model="spec.options" color="primary" hide-details/>
                      </v-flex>
                    </v-layout>
                  </v-container>

                  <v-container v-else grid-list-md fluid class="mt-0">
                    <v-layout wrap row justify-space-between class="px-4">
                      <v-flex xs12 sm6 v-for="i in spec.options.length+1" :key="i" class="row layout">
                        <v-text-field :placeholder="'新的' + spec.name + ':'" class="flex xs10" auto-grow
                                      v-model="spec.options[i-1]" v-bind:value="i" single-line hide-details/>
                        <v-btn @click="spec.options.splice(i-1,1)" v-if="i <= spec.options.length" icon>
                          <i class="el-icon-delete"/>
                        </v-btn>
                      </v-flex>
                    </v-layout>
                  </v-container>
                  <v-divider></v-divider>
                </v-card-text>
              </v-flex>
            </v-card>
          </v-card-text>
        </v-card>


      </v-stepper-content>
      <!--4、SKU属性-->
      <v-stepper-content step="4">
        <v-flex class="mx-auto">

          <v-card class="elevation-0">
            <!--标题-->
            <div class="subheading py-3">SKU列表:</div>
            <v-divider/>
            <!--SKU表格，hide-actions因此分页等工具条-->
            <v-data-table :items="skus" :headers="headers" hide-actions item-key="indexes" class="elevation-0">
              <template slot="items" slot-scope="props">
                <tr @click="props.expanded = !props.expanded">
                  <!--价格和库存展示为文本框-->
                  <td v-for="(v,k) in props.item" :key="k" v-if="['price', 'stock'].includes(k)"
                      class="text-xs-center">
                    <v-text-field single-line v-model="props.item[k]" @click.stop=""/>
                  </td>
                  <!--enable展示为checkbox-->
                  <td class="text-xs-center" v-else-if="k === 'enable'">
                    <v-checkbox v-model="props.item[k]"/>
                  </td>
                  <!--indexes和images不展示，其它展示为普通文本-->
                  <td class="text-xs-center" v-else-if="k !== 'images' && k !== 'indexes'&& k !== 'id'">{{v.v}}</td>
                </tr>
              </template>
              <!--点击表格后展开-->
              <template slot="expand" slot-scope="props">
                <v-card class="elevation-2 flex xs11 mx-auto my-2">
                  <!--图片上传组件-->
                  <v-upload v-model="props.item.images" url="/auth/ali/oss/signature" needSignature/>
                </v-card>
              </template>
            </v-data-table>
          </v-card>
        </v-flex>
        <!--提交按钮-->
        <v-flex xs3 offset-xs9>
          <v-btn color="info" @click="submit">保存商品信息</v-btn>
        </v-flex>
      </v-stepper-content>
    </v-stepper-items>
  </v-stepper>
</template>

<script>
  export default {
    name: "goods-form",
    props: {
      oldGoods: {
        type: Object
      },
      isEdit: {
        type: Boolean,
        default: false
      },
      step: {
        type: Number,
        default: 1
      }
    },
    data() {
      return {
        valid: false,
        goods: {
          categories: [], // 商品分类信息
          brandId: 0, // 品牌id信息
          name: "", // 标题
          title: "", // 子标题
          spuDetail: {
            packingList: "", // 包装列表
            afterService: "", // 售后服务
            description: "" // 商品描述
          }
        },
        brandOptions: [], // 品牌列表
        specs: [], // 规格参数的模板
        specialSpecs: [], // 特有规格参数模板
      };
    },
    methods: {
      submit() {
        if (this.isEdit) {
          this.handleEdit();
        } else {
          this.handleAdd();
        }
      },
      handleAdd() {
        // 表单校验。
        if (!this.$refs.basic.validate) {
          this.$message.error("请先完成表单内容！");
        }
        // 先处理goods，用结构表达式接收,除了categories外，都接收到goodsParams中
        const {
          categories: [{id: cid1}, {id: cid2}, {id: cid3}],
          ...goodsParams
        } = this.goods;
        // 处理规格参数
        const specs = {};
        this.specs.forEach(({id, v}) => {
          specs[id] = v;
        });
        this.specialSpecs.forEach(({id, options}) => {
          specs[id] = options;
        });
        // 处理sku
        const skus = this.skus
          .filter(s => s.enable)
          .map(({price, stock, enable, images, indexes, ...rest}) => {
            // 标题，在spu的name基础上，拼接特有规格属性值
            const title = goodsParams.name + " " + Object.values(rest).map(v => v.v).join(" ");
            const obj = {};
            Object.values(rest).forEach(v => {
              obj[v.id] = v.v;
            });
            return {
              price: this.$format(price), // 价格需要格式化
              stock,
              indexes,
              title, // 基本属性
              images: images ? images.join(",") : '', // 图片
              specialSpec: JSON.stringify(obj) // 特有规格参数
            };
          });
        Object.assign(goodsParams, {
          cid1,
          cid2,
          cid3, // 商品分类
          skus // sku列表
        });
        goodsParams.spuDetail.specification = JSON.stringify(specs);
        this.$http({
            method: this.isEdit ? "put" : "post",
            url: "/item/goods/spu",
            data: goodsParams
          })
          .then(() => {
            // 成功，关闭窗口
            this.$emit("close");
            // 提示成功
            this.$message.success("保存成功了");
          })
          .catch(() => {
            this.$message.error("保存失败！");
          });
      },
      handleEdit() {
        // 1.判断spu是否修改
        // 1.1.获取spu
        let {id, categories, brandId, name, title} = this.goods;
        let spu = {id, brandId, name, title, cid1: categories[0].id, cid2: categories[1].id, cid3: categories[2].id};
        let spuTitle = title;
        // 1.2.判断spu是否有修改
        const isSpuChange = !Object.compareObj(spu, this.oldGoods);
        if (!isSpuChange) {
          spu = {};
        }
        // 2.判断spuDetail是否修改

        let {...detail} = this.goods.spuDetail;
        const specs = {};
        this.specs.forEach(({id, v}) => {
          specs[id] = v;
        });
        this.specialSpecs.forEach(({id, options}) => {
          specs[id] = options;
        });
        detail.specification = JSON.stringify(specs);
        // 2.3.判断spuDetail是否有修改
        let isDetailChange = false;
        for (let k in detail) {
          if(detail[k] !== this.oldGoods.spuDetail[k]){
            isDetailChange = true;
          }else{
            delete detail[k];
          }
        }
        if (isDetailChange) {
          detail.spuId = this.goods.id;

          spu.spuDetail = detail;
        }
        // 3.判断sku是否修改
        // 3.1.处理sku
        const skus = this.skus
          .map(({title, price, stock, enable, images, indexes, id, ...rest}) => {
            if(!enable){
              return {id, enable:false}
            }
            const obj = {};
            Object.values(rest).forEach(v => {
              obj[v.id] = v.v;
            });
            return {
              id,
              title: title || spuTitle + " " + Object.values(obj).join(" "),
              price: this.$format(price), // 价格需要格式化
              stock,
              indexes,
              images: images ? images.join(",") : '', // 图片
              specialSpec: JSON.stringify(obj) // 特有规格参数
            };
          });
        // 3.3.判断sku是否修改
        let isSkuChange = false;
        for (let i = 0; i < skus.length; i++) {
          let s = skus[i];
          let os = this.oldGoods.skus.find(v => v.id === s.id);
          if (!Object.compareObj(s, os)) {
            isSkuChange = true;
            if(!os && s.enable === false){
              skus.splice(i, 1);
              i--;
            }else if(os && s.enable === false){
              s.saleable = false;
            }
          }else{
            skus.splice(i, 1);
            i--;
          }
          s.spuId = id;
        }
        if (isSkuChange) {
          spu.skus = skus;
        }
        if (isSpuChange || isSkuChange || isDetailChange) {
          // 4.发起请求
          this.$http.put("/item/goods/spu", spu)
            .then(() => {
              // 成功，关闭窗口
              this.$emit("close");
              // 提示成功
              this.$message.success("保存成功了");
            })
            .catch(() => {
              this.$message.error("保存失败！");
            });
        } else {
          // 成功，关闭窗口
          this.$emit("close");
          // 提示成功
          this.$message.warning("商品没有修改！");
        }
      }
    },
    watch: {
      oldGoods: {
        deep: true,
        handler(val) {
          if (!this.isEdit) {
            Object.assign(this.goods, {
              categories: null, // 商品分类信息
              brandId: 0, // 品牌id信息
              name: "", // 标题
              title: "", // 子标题
              spuDetail: {
                packingList: "", // 包装列表
                afterService: "", // 售后服务
                description: "" // 商品描述
              }
            });
            this.specs = [];
            this.specialSpecs = [];
          } else {
            this.goods = Object.deepCopy(val);

            // 先得到分类名称
            const names = val.categoryName.split("/");
            delete this.goods.categoryName;
            delete this.goods.brandName;
            delete this.goods.createTime;
            delete this.goods.saleable;
            // 组织商品分类数据
            this.goods.categories = [
              {id: val.cid1, name: names[0]},
              {id: val.cid2, name: names[1]},
              {id: val.cid3, name: names[2]}
            ];

            // 将skus处理成map
            const skuMap = new Map();
            this.goods.skus.forEach(s => {
              s.enable = true;
              skuMap.set(s.indexes, s);
            });
            this.goods.skus = skuMap;
          }
        }
      },
      "goods.categories": {
        deep: true,
        handler(val) {
          // 判断商品分类是否存在，存在才查询
          if (val && val.length > 0) {
            // 根据分类查询品牌
            this.$http
              .get("/item/brand/of/category?id=" + this.goods.categories[2].id)
              .then(({data}) => {
                this.brandOptions = data;
              });
            // 根据分类查询规格参数
            this.$http
              .get("/item/spec/params?categoryId=" + this.goods.categories[2].id)
              .then(({data}) => {
                let specs = [];
                let template = [];
                if (this.isEdit) {
                  specs = JSON.parse(this.goods.spuDetail.specification);
                }
                // 对特有规格进行筛选
                const arr1 = [];
                const arr2 = [];
                data.forEach(({id, name, generic, numeric, unit, options}) => {
                  if (generic) {
                    const o = {id, name, numeric, unit, ops: options ? options.split(',') : []};
                    if (this.isEdit) {
                      o.v = specs[id];
                    }
                    arr1.push(o)
                  } else {
                    const o = {id, name, options: [], ops: options ? options.split(',') : []};
                    if (this.isEdit) {
                      o.options = specs[id];
                    }
                    arr2.push(o)
                  }
                });
                this.specs = arr1;// 通用规格
                this.specialSpecs = arr2;// 特有规格
              });
          }
        }
      },
      goods: {
        deep: true,
        handler() {
          if (this.isEdit) {
            this.modified = true;
          }
        }
      }
    },
    computed: {
      skus() {
        // 过滤掉用户没有填写数据的规格参数
        const arr = this.specialSpecs.filter(s => s.options.length > 0);
        // 通过reduce进行累加笛卡尔积
        return arr.reduce(
          (last, spec, index) => {
            const result = [];
            last.forEach(o => {
              spec.options.forEach((option, i) => {
                const obj = JSON.parse(JSON.stringify(o));
                obj[spec.name] = {v: option, id: spec.id};
                obj.indexes = (obj.indexes || '') + '_' + i
                if (index === arr.length - 1) {
                  obj.indexes = obj.indexes.substring(1);
                  // 如果发现是最后一组，则添加价格、库存等字段
                  Object.assign(obj, {
                    price: 0,
                    stock: 0,
                    enable: false,
                    images: []
                  });
                  if (this.isEdit) {
                    // 如果是编辑，则回填sku信息
                    const sku = this.goods.skus.get(obj.indexes);
                    if (sku != null) {
                      const {title, price, stock, enable, images, id} = sku;
                      Object.assign(obj, {
                        id,
                        price: this.$format(price),
                        title,
                        stock,
                        enable,
                        images: images ? images.split(",") : [],
                      });
                    }
                  }
                }
                result.push(obj);
              });
            });
            return result;
          },
          [{}]
        );
      },
      headers() {
        if (this.skus.length <= 0) {
          return [];
        }
        const headers = [];
        Object.keys(this.skus[0]).forEach(k => {
          let value = k;
          let width = "";
          if (k === "price") {
            // enable，表头要翻译成“价格”
            k = "价格";
            width = "120px";
          } else if (k === "stock") {
            // enable，表头要翻译成“库存”
            width = "120px";
            k = "库存";
          } else if (k === "enable") {
            // enable，表头要翻译成“是否启用”
            width = "80px";
            k = "是否启用";
          } else if (k === "images" || k === 'indexes' || k === 'id' || k === 'title') {
            // 图片和索引不在表格中展示
            return;
          }
          headers.push({
            text: k,
            align: "center",
            sortable: false,
            value,
            width
          });
        });
        return headers;
      }
    }
  };
</script>

<style scoped>
</style>
