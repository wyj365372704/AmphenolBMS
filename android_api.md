#Amphenol_android开发接口文档 version 2.5

##书写说明
- api采用json数据格式返回
- 为做说明，采用 keyName ：valueTyep 形式说明json键值对类型，如：code ： int	返回码 ，从左往右依次为 key名称、value数据类型、value描述。

##公共请求字段
1.username 用户名

2.env 使用环境

注：除了特别说明参数为null ，所有api参数都要接上公共请求字段

##公共返回字段
1.code : int 返回码
	
	- 1：成功
	- 0：登录过期
	- 999：服务器错误


2.dese ： String 返回描述

	"ok"	code=1时统一返回 

注：所有api返回都要接上公共返回字段

##获取环境列表
	请求方式：get
	参数：null
	action=get_env
	示例：http://192.168.0.106:8080/AmphenolBMS/resource/resource!get_env.action

返回

	env_list:List<String>	环境集合

	示例：
	{
	    "code": 1,
	    "desc": "ok",
	    "env_list": [
			"M1",
			"M2",
			"M3"
		]
	}


##登录
	请求方式：get
	参数：password 密码
	action=login_check
	示例：http://192.168.0.106:8080/AmphenolBMS/resource/resource!login_check.action?username=k123456789&env=abcdefg&password=1
返回

	code = 2 用户名或密码错误
	code = 3 环境错误

##功能菜单获取
	请求方式：get
	参数：default
	action=get_menu

返回

	menu_list: List<Map<Strig,String>>  菜单编号
		11：采购收货
		12：采购退货
		31:创建调拨单
		32:审核调拨单
		33:快速调拨

	示例：
	{
	    "code": 1,
	    "desc": "ok",
	    "menu_list": [
	        {
	            "11": "采购收货",
	            "12": "采购退货"
	        },
	        {
	            "21": "仓库入库",
	            "22": "仓库出库"
	        }
	    ]
	}


##获取仓库及仓库列表
	请求方式：get
	参数：
		default
	action=query_warehouse

返回

	warehouse:String 默认仓库
	warehouse_list:List<Map<String,Object>> 仓库列表集合
		Map<String,Object> 仓库
			name:String 仓库名
		
##获取子库列表
	请求方式：get
	参数：
		warehouse	仓库
	action=query_shard_list

返回
	
	shard_list:List<Map<String,Object>> 子库列表集合
		Map<String,Object> 子库
			name:String 子库名

##采购收货-查询送货单
	请求方式：get
	参数：delive_code	供应商送货单号码
	action=query_receipt

返回

	firm:String	送货厂商
	receipt_number:String 送货单号码
	status_code:int	收货单状态
		10：未收货
		40：已接收,未入库
		50：已入库
	mater_list:List<Map<String,Object>> 物料集合
		Map<String,Object>
			mater_po: String 采购单-项次
			number: String 收货单行号
			mate: String 物料编号
			quantity： Double 计划数量
			unit： String 单位
	示例：
	{
	    "firm": "送货厂商",
	    "mater_list": [
	        {
	            "mate": "mate001",
	            "number": "123",
	            "quantity": 600,
	            "unit": "KG"
	        },
	        {
	            "mate": "mate002",
	            "number": "124",
	            "quantity": 700,
	            "unit": "G"
	        }
	    ],
	    "receipt_number": "P-1234567",
	    "status_code": 1
	}

##采购收货-查询物料明细
	请求方式：get
	参数：
		receipt_number	送货单号码
		receipt_line	收货单行号
	action=query_receipt_item

返回

	mate_number:String	物料编号
	mate_desc:String 物料描述
	purchase_unit:String 采购单位
	plan_quantity:double 计划数量
	branch_control:	int 批次管控
		1：控制
		0：不控制
	actual_single:double 实际单重
	actual_unit:String 实际单重单位
	location:String 收货库位
	branch_list:List<Map<String,Object>> 批次集合 ，若没有不返回
		Map<String,Object>
			branch_number: String 到货单批明细行
			branch_desc:String 生产批次
			plan_quantity:double 计划数量

##关闭物料收货
	请求方式：get
	参数：
		receipt_number	送货单号码
		receipt_line	收货单行号
	action=mate_receipt_close

返回

	code = 1 成功

##确认采购收货
	请求方式：get
	参数：
		receipt_number	送货单号码
		receipt_line	收货单行号
		actual_single 实际单重
		actual_single_update 是否更新实际单重
			1：更新
			other：不更新
		actual_quantity 实际总数
		location 收货库位
		branch_list	批次信息集合的json字符串，如不受批次控制不附带，服务器端进行json解析。说明如下
			branch_list:List<Map<String,Object>> 按此生成json字符串
				Map<String,Object>
					branch_number: String 到货单批明细行	（如果是新增批次，置为“-1”）
					branch_desc:String 生产批次
					plan_quantity:double 计划数量
					actual_quantity:double 实际数量	若实际数量为0，则表示删除此批次
	action=mate_receipt_confirm

返回

	code = 1 成功
	code = 2 新增加的生产批次已存在
	code = 3 收货库位不存在
	code = 4 实际单重更新失败
	code = other 确认收货失败


##采购入库-查询送货单
	请求方式：get
	参数：delive_code	供应商送货单号码
	action=query_receipt_storage

返回

	firm:String	送货厂商
	receipt_number:String 送货单号码
	status_code:int	到货单状态
		10 已创建, 未收货
		40 部分完成收货 或 关闭
		50 全部完成收货 或 关闭
	mater_list:List<Map<String,Object>> 物料集合
		Map<String,Object>
			mater_po: String 采购单-项次
			number: String 收货单行号
			mate: String 物料编号
			quantity： Double 实际接收数量
			unit： String 单位


##采购入库-查询物料明细
	请求方式：get
	参数：
		receipt_number	送货单号码
		receipt_line	收货单行号
	action=query_receipt_storage_item

返回

	mate_number:String	物料编号
	mate_desc:String 物料描述
	purchase_unit:String 采购单位
	plan_quantity:double 计划总数
	actual_quantity:double 实际接收数量
	branch_control:	int 批次管控
		1：控制
		0：不控制
	actual_single:double 实际单重
	actual_unit:String 实际单重单位
	location:String 入库库位
	branch_list:List<Map<String,Object>> 批次集合 ，若没有不返回
		Map<String,Object>
			branch_number: String 到货单批明细行
			branch_desc:String 生产批次
			plan_quantity:double 实际接收数量

##确认采购入库
	请求方式：get
	参数：
		receipt_number	送货单号码
		receipt_line	收货单行号
		actual_single 实际单重
		actual_single_update 是否更新实际单重
			1：更新
			other：不更新
		actual_quantity 实际总数
		location 收货库位
		branch_list	批次信息集合的json字符串，如不受批次控制不附带，服务器端进行json解析。说明如下
			branch_list:List<Map<String,Object>> 按此生成json字符串
				Map<String,Object>
					branch_number: String 到货单批明细行	（如果是新增批次，置为“-1”）
					branch_desc:String 生产批次
					plan_quantity:double 计划数量
					actual_quantity:double 实际数量	若实际数量为0，则表示删除此批次
	action=mate_receipt_storage_confirm

返回

	code = 1 成功
	code = 2 新增加的生产批次已存在
	code = 3 收货库位不存在
	code = 4 实际单重更新失败
	code = other 确认收货失败


##查询退货单
	请求方式：get
	参数：return_number	供应商退货单号码
	action=query_return

返回

    code : int 
        6 : 查无退货单
	firm:String	供应商
	status_code:int	退货单状态
		05 : 创建中
        10 : 已创建, 未退货
        40 : 部分已经退货
        50 : 全部完成退货
	mater_list:List<Map<String,Object>> 物料集合
		Map<String,Object>
			mater_po: String 采购单-项次
			number: String 退货单行号
			mater: String 物料编号
			quantity： Double 退货数量
			unit： String 单位


##退货-查询物料明细
	请求方式：get
	参数：
		mater	物料编号
	action=query_return_item

返回

	mate_desc:String 物料描述
	location_list:List<Map<String,Object>> 库位集合
		Map<String,Object>  
			shard : String 子库
			location : String 库位
			branch :String 生产批次
			quantity:double 库存数量


##关闭物料退货
	请求方式：get
	参数：
		return_number	退货单号码
		return_line	退货单行号
	action=mater_return_close

返回

	code = 4 失败


##确认物料退货
	请求方式：get
	参数：
		return_number	退货单号码
		return_line	退货单行号
		mater_list	物料信息集合的json字符串。说明如下
			mater_list:List<Map<String,Object>> 按此生成json字符串
				Map<String,Object>
				    location : String 库位
					branch_desc:String 生产批次
					return_quantity:double 退货数量
	action=mater_return_confirm

返回

    default

##创建调拨单/快速调拨/库存查询/盘点 - 查询物料列表
	请求方式：get
	参数：
		warehouse	仓库
		shard	子库,允许为空
		location	库位，允许为空
		mate 物料编号 ,允许为空
	action=create_requisition_get_mater_list

返回
	
	mater_list:List<Map<String,Object>> 物料批次集合。注意，区分精度控制到批次层面。例如：物料P-1234有三个批次BP-1、BP-2、BP-3，则将三条记录分开返回。
		Map<String,Object>
			mate: String 物料编号
			branch:批次号
			shard:String 当前子库
			location:String 当前库位
			target_shard:String	目标子库
			target_location:String	目标库位
			quantity： Double 库存数量
			unit： String 库存单位
	code 
		5 没有结果集

##创建调拨单/快速调拨 - 查询物料明细
	请求方式：get
	参数：
		warehouse 仓库
		shard 子库
		location 库位
		mate 物料编号
		branch 批次号
	action=create_requisition_get_mater

返回

	mater_desc:String 物料描述
	mater_format:String	物料规格
	target_shard:String	目标子库
	target_location:String	目标库位
	
##创建调拨单 - 提交
	请求方式：get
	参数：
		mater_list 调拨物料批次集合的json字符串，服务器进行json解析，说明如下
			mater_list：List<Map<String,Object>> 调拨物料列表，按此生成json字符串
				Map<String,Object>:调拨物料描述
					from_warehouse:String 来源仓库
					from_shard:String 来源子库
					from_location:String 来源库位
					target_warehouse:String 目标仓库
					target_shard:String 目标子库
					target_location:String 目标库位
					mater:String 物料编号
					branch:String 批次号
					quantity:double 调拨数量，调拨数量不允许大于该物料批次的库存数量
	action=create_requisition_commit
		
返回
	default

##快速调拨 - 提交

	请求方式：get
	参数：
		mater_list 调拨物料批次集合的json字符串，服务器进行json解析，说明如下
			mater_list：List<Map<String,Object>> 调拨物料列表，按此生成json字符串
				Map<String,Object>:调拨物料描述
					from_warehouse:String 来源仓库
					from_shard:String 来源子库
					from_location:String 来源库位
					target_warehouse:String 目标仓库
					target_shard:String 目标子库
					target_location:String 目标库位
					mater:String 物料编号
					branch:String 批次号
					quantity:double 调拨数量，调拨数量不允许大于该物料批次的库存数量
	action=fast_requisition_commit
		
返回
	
	code 
		5 目标库位不存在


##审核调拨单

	请求方式：get
	参数：
		requisition 调拨单号
	action=check_requisition_get_mater_list

返回

	state:int 调拨单状态
		10 已创建
		60 已关闭
	founder:String 创建人
	department:String 创建部门
	date：String 创建日期 ，注意返回格式为：(yyyy-MM-dd HH:mm:ss)
	mater_list:List<Map<String,Object>> 物料集合
		Map<String,Object> 物料
			requisition_line:int 调拨单行
			mater:String 物料
			branch:Striing	批次
			quantity:double	调拨数量
			unit:String	单位
	code
		5 查无此调拨单
	
##审核调拨单-物料明细查询
	
	请求方式：get
	参数：
		requisition 调拨单号
		requisition_line 调拨单行		
	action=check_requisition_get_mater_detail

返回
	
	mater_desc:String 物料描述
	mater_format:String	物料规格
	from_warehouse:String	来源仓库
	form_shard:String 来源子库
	form_location:String	来源库位
	target_shard:String	目标子库
	target_location:String	目标库位
	target_shard_list:List<Map<String,Object>> 目标子库列表
		Map<String,Object> 子库
			shard:String 子库名
			
		
##审核调拨单-确认过账
	
	请求方式：get
	参数：
		requisition 调拨单号
		requisition_line 调拨单行	
		actual_quantity 实收数量，double类型
		target_warehouse 目标仓库
		target_shard 目标子库
		target_location 目标库位
		update 是否更新目标子库或库位
			0 不更新
			1 更新
	action=check_requisition_sure

返回

	code = 1 成功
	code = 5 目标子库和库位不匹配

##审核调拨单-终止过账

	请求方式：get
	参数：
		requisition 调拨单号
		requisition_line 调拨单行	
	action=check_requisition_cancel

返回

	default

##生产发料 - 领料项查询

	请求方式：get
	参数：
		pick_number 领料单单号
	action=hair_mater_get_pick_list

返回

	work_order:String 工单号
	founder:String 创建人
	department:String 创建部门
	date：String 创建日期 ，注意返回格式为：(yyyy-MM-dd HH:mm:ss)
	type:Int 领料单类型
		1 = 正常领料单
		2 = 超发领料单
		3 = 退料领料单
	state:Int 领料单状态
		5 = 创建中
		10 = 已创建
		50 = 领料已经完成
	picking_list:List<Map<String,Object>> 领料项集合
		Map<String,Object> 领料项
			pick_line:String 领料单行号
			sequence:String 系统序列号
			mater:String 物料
			quantity:double 计划数量/退料数量
			unit:String 材料单位 
			warehouse:String 仓库
			shard:String 默认子库
			location:String 默认库位
			state:Int 领料单行状态
				5 = 创建中
				10 = 已创建
				50 = 已完成
				60 = 已关闭(未发料)
			branched:int 是否需要批次控制
				0 不需要
				1 需要
	code
		5 = 无效领料单

##生产发料 - 查询物料列表
	请求方式：get
	参数：
		warehouse	仓库
		pick_number 领料单单号
		pick_line 领料单行号
		mater 物料编号
		shard	子库,允许为空
		location	库位，允许为空
		branch	批号,允许为空
	action=hair_mater_get_mater_list

返回

	mater_dese:String 物料描述
	mater_format:String 物料规格
	shard_list:List<Map<String,Object>> 仓库下属的子库集合
		Map<String,Object> 子库
			shard:String 子库名
	mater_list:List<Map<String,Object>> 物料批次集合。注意，区分精度控制到批次层面。例如：物料P-1234有三个批次BP-1、BP-2、BP-3，则将三条记录分开返回。此外，mater_list需要按照fifo_date的升序规则进行排列。
		Map<String,Object>
			branch:批次号
			shard:String 当前子库
			location:String 当前库位
			quantity： Double 库存数量
			unit： String 库存单位
			fifo_date : Long 先进先出日期 FIFO Date
	code 
		5 没有结果集

##生产发料 - 发料过账
	请求方式:get
	参数:
		warehouse	仓库	
		department	生产部门
		work_order	工单号
		sequence	系统顺序号
		pick_number 领料单单号
		pick_line 领料单行号
		actual_quantity 发料总数量
		mater_list 发料物料批次集合的json字符串，服务器进行json解析，说明如下
			mater_list：List<Map<String,Object>> 发料物料批次集合，按此生成json字符串
				Map<String,Object>:物料
					mater:String	物料编码
					branch:String	批次,如果为空或者不存在,表示该物料不受批次管控
					shard : String 发料子库
					location:String	发料库位
					quantity:double	发料数量
	action=hair_mater_submit

返回

	default

##生产发料 - 退料过账
	请求方式:get
	参数:
		warehouse	仓库	
		department	生产部门
		work_order	工单号
		sequence	系统顺序号
		pick_number 领料单单号
		pick_line 领料单行号
		actual_quantity 退料实收总数量
		mater:String	物料编码
		shard : String  退料子库
		location:String	退料库位
		branch_list	批次信息集合的json字符串，如不受批次控制不附带，服务器端进行json解析。说明如下
			branch_list:List<Map<String,Object>> 按此生成json字符串
				Map<String,Object>
					branch_number:String 生产批次号
					branch_quantity:double 实收数量
	action=hair_mater_return_submit

返回

	default

	
##生产发料 - 终止过账
	请求方式:get
	参数:
		warehouse	仓库	
		department	生产部门
		work_order	工单号
		sequence	系统顺序号
		pick_number 领料单单号
		pick_line 领料单行号
	action=hair_mater_cancel

返回
	
	default
		

##生产入库 - 工单查询
	请求方式:get
	参数:
		warehouse	仓库	
		work_order	工单号
	action=production_storage_inquire

返回

	order_state : int 订单状态
		10 = 已下达; 40 = 已开始生产
		45 = 物料完成; 50 = 工序完成
		55 = 物料/工序完成; 99 = 订单取消
	product_desc : String 产品描述
	product : String 产品
	branched:int 是否需要批次控制
		0 不需要
		1 需要
	quantity_order : double 订单数量
	quantity_storaged : double 已入库数量	
	unit : String 单位
	shard : String 默认子库
	location : String 默认库位
	
##生产入库 - 确认入库
	请求方式:get
	参数:
		work_order	工单号
		branch 批次号 ,如果受批次控制测附带,否则为空
		quantity 入库总数
		warehouse 目标仓库
		shard 目标子库
		location 目标库位
		update 是否更新目标子库或库位
			0 不更新
			1 更新
	action=production_storage_submit

返回

	default
	code = 7 入库数量超过上限
	max_remain : double 尚可入库的数量


##生产订单查询
	请求方式:get
	参数:
		warehouse	仓库	
		work_order	工单号
	action=production_order_inquire

返回

	department : String 部门
	order_state : int 订单状态
		10 = 已下达; 40 = 已开始生产
		45 = 物料完成; 50 = 工序完成
		55 = 物料/工序完成; 99 = 订单取消
	product_name : String 产品名称
	product_desc : String 产品描述
	product_form : String 产品规格
	quantity_order_product : double 生产订单数量
	quantity_finished_product : double 生产完工数量
	quantity_remain_product : double 生产剩余数量
	plain_start_date : String 计划开始日期 ，注意返回格式为：(yyyy-MM-dd HH:mm:ss)
	plain_finish_date : String 计划完成日期 ，注意返回格式为：(yyyy-MM-dd HH:mm:ss)
	actual_start_date : String 实际开始日期 ，注意返回格式为：(yyyy-MM-dd HH:mm:ss)
	sale_order_number : String 销售订单号
	customer_name : String 客户名称
	customer_code : String 客户代码
	customer_purchase_order_number : String 客户采购订单号
	quantity_order_sale : double 销售订单数量
	quantity_shipmented_sale : double 出货数量
	quantity_remain_sale : double 销售剩余数量
	compliance_date : String 承诺日期
	mater_list : String 订单材料明细信息集合json字符串
		mater_list : List<Map<String,Object>> 按此生成json字符串
			Map<String,Object> 材料明细单位
				mater_number : String 材料序号
				mater_name : String 材料编码
				mater_desc : String 材料描述
				mater_form : String 材料规格
				plan_usage_amount : double 计划用量
				actual_usage_amount : double 实际用量
				mater_unit : String 材料单位
				last_hair_mater_date : String 最后一次发料时间  ，注意返回格式为：(yyyy-MM-dd HH:mm:ss)
	step_list : String 订单工序明细信息集合json字符串
		step_list : List<Map<String,Object>> 按此生成json字符串
			Map<String,Object> 工序明细单位
				step_number : String 工序编号
				step_name : String 工序名称
				standard_working_hours : double 标准工时
				actual_working_hours : double 实际工时
				outsourcing_costs : double 外协成本
				outsourcing_supplier : String 外协供应商
				outsourcing_purchase_order_number : String 外协采购订单号
				tbc : String Time Basis Code


##生产订单查询 - 工序外协信息(补充接口)
	请求方式:get
	参数:
		work_order	工单号
		step_number 工序编号
	action=production_order_inquire_get_step_outsource_info

返回

	outsourcing_costs : double 外协成本
	outsourcing_costs_unit : double 外协成本单位
	outsourcing_supplier : String 外协供应商
	outsourcing_purchase_order_number : String 外协采购订单号
	

##打印物料标签-物料查询
	请求方式:get
	参数:
		mater 物料编号
	action=print_mater_label_inquire

返回

	mater_format : String 物料规格
	mater_desc : String 物料描述
	branched : int 批次控制
		1 = 控制
		0 = 不控制
	storage_unit : String 数量单位(库存单位)
	single : double 单重
	single_unit : String 单重单位


##生产报工-新增作业-生产订单号查询
	请求方式:get
	参数:
		work_order 生产订单号
	action=production_report_add_new_job_work_order_inquire

返回
	code : int
		5 该订单不存在工序信息
		6 不存在生产线
		7 该订单不存在
		
	step_list : List<Map<String,Object>> 订单工序信息集合json字符串
		Map<String,Object> 工序明细单位
			step_number : String 工序编号
			step_name : String 工序名称
	propr_list : List<Map<String,Object>> 按此生成json字符串
		Map<String,Object> 生产线明细单位
			propr_number : String 生产线编号
			propr_name : String 生产线名称

##生产报工-新增作业-获取员工列表
	请求方式:get
	参数:
		work_order 生产订单号
		step_number 工序号
		propr_number 生产线号
	action=production_report_add_new_job_employee_inquire

返回

	code : int 
		5 已添加了该订单+工序+生产线 的作业任务,此时其他信息不再返回
	employee_list : List<Map<String,Object>> 员工信息集合json字符串.注意:员工排序按照默认选中优先排前规则,所列项目的状态为空闲
		Map<String,Object> 员工信息单位
			employee_number : String 员工编号
			employee_name : String 员工名称
			employee_dept : String 所属部门
			default_checked : int 默认选中
				0 : 否
				1 : 是


##生产报工-新增作业-获取设备列表
	请求方式:get
	参数:
		work_order 生产订单号
		step_number 工序号
		propr_number 生产线号
	action=production_report_add_new_job_machine_inquire

返回

	code : int 
		5 已添加了该订单+工序+生产线 的作业任务,此时其他信息不再返回
	machine_list : List<Map<String,Object>> 设备信息集合json字符串.注意:设备排序按照默认选中优先排前规则,所列项目的状态为空闲
		Map<String,Object> 设备信息单位
			machine_number : String 设备编号
			machine_name : String 设备名称
			machine_dept : String 所属部门
			default_checked : int 默认选中
				0 : 否
				1 : 是


##生产报工-新增作业-提交
	请求方式:get
	参数:
		work_order 生产订单号
		step_number 工序号
		propr_number 生产线号
		begin_time 开始时间 ，注意格式为：(yyyy-MM-dd HH:mm:ss)
		employee_list	员工信息集合的json字符串，服务器端进行json解析。说明如下
			employee_list : List<Map<String,Object>> 按此生成json字符串
				Map<String,Object>
					employee_number : String 员工编号
		machine_list	设备信息集合的json字符串，服务器端进行json解析。说明如下
			machine_list : List<Map<String,Object>> 按此生成json字符串
				Map<String,Object>
					machine_number : String 设备编号
	action=production_report_add_new_job_submit

返回

	code : int 
		5 已添加了该订单+工序+生产线 的作业任务,此时其他信息不再返回
		6	生产订单不存在
	default
	

##生产报工-获取作业列表
	请求方式:get
	参数:
		default
	action=production_report_get_job_list

返回

	job_list : String 作业信息集合json字符串.注意:作业排序按照生成时间倒序排序
		job_list : List<Map<String,Object>> 按此生成json字符串
			Map<String,Object> 设备信息单位
				job_number : String 作业号
				work_order : String 生产订单号
				step_name : String 工序名称
				step_number : String 工序编号
				proper_name : String 生产线名称
				proper_number : String 生产线编号


##生产报工-获取作业详细
	请求方式:get
	参数:
		job_number 作业号
	action=production_report_get_job_detail

返回

	code : int
		5 作业不存在
	work_order : String 生产订单号
	department : String 生产部门
	create_time : String 创建时间  ，注意返回格式为：(yyyy-MM-dd HH:mm:ss)
	employee_list : List<Map<String,Object>> 员工信息集合的json字符串。注意:所列项目的状态为工作中
		Map<String,Object>
			employee_number : String 员工编号
			employee_name : String 员工姓名
			begin_time : String 开始时间  ，注意返回格式为：(yyyy-MM-dd HH:mm:ss)
	machine_list : List<Map<String,Object>> 设备信息集合的json字符串。注意:所列项目的状态为工作中
		Map<String,Object>
			machine_number : String 设备编号
			machine_name : String 设备名称
			begin_time : String 开始时间  ，注意返回格式为：(yyyy-MM-dd HH:mm:ss)


##生产报工-查询员工信息
	请求方式:get
	参数:
		work_order 生产订单号
		step_number 工序号
		propr_number 生产线号
		job_number 作业号
		employee_number 员工编号
	action=production_report_employee_inquire

返回

	code : Int 
		5 无该员工信息
		6 该员工不在当前作业可支配范围
	name : String 员工姓名
	state : String 当前状态
	    0 空闲
	    1 忙碌
	department : String 所属部门


##生产报工-查询设备信息
	请求方式:get
	参数:
		work_order 生产订单号
		step_number 工序号
		propr_number 生产线号
		job_number 作业号
		machine_number 设备编号
	action=production_report_machine_inquire

返回

	code : Int 
		5 无该设备信息
		6 该设备不在当前作业可支配范围
	name : String 设备名称
	state : String 当前状态
	    0 空闲
	    1 忙碌
	department : String 所属部门


##生产报工-中途加入员工
	请求方式:get
	参数:
		work_order 生产订单号
		step_number 工序号
		propr_number 生产线号
		job_number 作业号
		employee_number 员工编号
		warehouse 仓库
	action=production_report_employee_add

返回
	code : Int 
		5 无该员工信息
		6 该员工不在当前作业可支配范围
		7 该员工当前忙碌
	begin_time : String 开始时间  ，注意返回格式为：(yyyy-MM-dd HH:mm:ss)

##生产报工-中途加入设备
	请求方式:get
	参数:
		work_order 生产订单号
		step_number 工序号
		propr_number 生产线号
		job_number 作业号
		machine_number 设备编号
		warehouse 仓库
	action=production_report_machine_add

返回

	begin_time : String 开始时间  ，注意返回格式为：(yyyy-MM-dd HH:mm:ss)

##生产报工-中途离开员工
	请求方式:get
	参数:
		work_order 生产订单号
		step_number 工序号
		propr_number 生产线号
		job_number 作业号
		employee_number 员工编号
	action=production_report_employee_remove

返回

	default


##生产报工-中途离开设备
	请求方式:get
	参数:
		work_order 生产订单号
		step_number 工序号
		propr_number 生产线号
		job_number 作业号
		machine_number 设备编号
	action=production_report_machine_remove

返回

	default

##生产报工-开始作业
	请求方式:get
	参数:
		work_order 生产订单号
		step_number 工序号
		propr_number 生产线号
		job_number 作业号
	action=production_report_job_begin

返回

	default

##生产报工-结束作业-计算公式查询
	请求方式:get
	参数:
		job_number 作业号
	action=production_report_job_finish_inquire

返回

	artificial_hours : double 人工工时
	machine_hours : double 机器工时

##生产报工-结束作业-提交
	请求方式:get
	参数:
		job_number 作业号
		step_quantity 工序数量
		artificial_hours_after 调整后人工工时
		machine_hours_after 调整后机器工时
		abnormal_hours 异常工时
		abnormal_reason 异常原因
	action=production_report_job_finish_submit

返回

	default


##物料盘点-确认
	请求方式:get
	参数:
	    warehouse 仓库
		mater 物料号
		location 库位
		branch 批次
		ia_quantity IA调整数量
	action=inventory_update_submit

返回

    code : int
        5 库位不存在
	default


##盘点-物料基础信息查询-新增盘点
	请求方式:get
	参数:
	    warehouse 仓库
		mater 物料号
	action=inventory_add_query

返回

    code : int
        6 未找到该物料
	mater_desc:String 物料描述
	mater_format:String	物料规格
    branched:int 是否需要批次控制
    	0 不需要
		1 需要
    unit： String 库存单位


##销售出货-扫描出货通知单
	请求方式:get
	参数:
	    warehouse 仓库
		pldno 出货通知单号
	action=sale_shipment_query_header

返回

	code : int
		5 出货通知单不存在
		400 其他异常
	client_number : String 客户编码
	client_name : String 客户名称
	department : String 创建部门
	expected_data : String 预计出货日期  ，注意返回格式为：(yyyy-MM-dd)
	zpldtl_list : List<Map<String,Object>> 出货通知单明细集合的json字符串。注意:所列项目的状态为出货未完成
		Map<String,Object>
			pldln : String 出货通知单明细
			c6cvnb : String 客户订单号
			cdfcnb : String 客户订单行号
			mater : String 物料
			mater_dese:String 物料描述
			mater_format:String 物料规格
			shard:String 计划子库
			location:String 计划库位
			plan_quantity : double 计划数量
			plan_quantity_unit : String 计划数量单位
			

##销售出货-查看明细
	请求方式:get
	参数:
	    warehouse 仓库
		pldno 出货通知单号
		pldln 出货通知单明细
		mater 物料编号
		shard	子库,允许为空
		location	库位，允许为空
	action=sale_shipment_query_item

返回

	code : int
		5 出货通知单不存在
		400 其他异常
	boxln : int 箱明细行号
	boxnm : String 箱号
	boxes : int 箱数
	shard_list:List<Map<String,Object>> 仓库下属的子库集合
		Map<String,Object> 子库
			shard:String 子库名
	mater_list:List<Map<String,Object>> 物料批次集合。注意，区分精度控制到批次层面。例如：物料P-1234有三个批次BP-1、BP-2、BP-3，则将三条记录分开返回。此外，mater_list需要按照fifo_date的升序规则进行排列。
		Map<String,Object>
			branch:批次号
			shard:String 当前子库
			location:String 当前库位
			quantity： Double 库存数量
			unit： String 库存单位
			fifo_date : Long 先进先出日期 FIFO Date


##销售出货 - 确认出货
	请求方式:get
	参数:
		warehouse	仓库	
		pldno 出货通知单号
		pldln 出货通知单明细
		actual_quantity 出货总数量
		boxln : int 箱明细行号
		boxnm : String 箱号
		boxes : int 箱数
		mater_list 出货物料批次集合的json字符串，服务器进行json解析，说明如下
			mater_list：List<Map<String,Object>> 出货物料批次集合，按此生成json字符串
				Map<String,Object>:物料
					mater:String	物料编码
					branch:String	批次,如果为空或者不存在,表示该物料不受批次管控
					shard : String 子库
					location:String	库位
					quantity:double	数量
	action=sale_shipment_ensure

返回

	default


	
##销售出货 - 终止出货
	请求方式:get
	参数:
		warehouse	仓库	
		pldno 出货通知单号
		pldln 出货通知单明细
	action=sale_shipment_cancel

返回
	
	default


	
##销售出货 - 出货过账
	请求方式:get
	参数:
		warehouse	仓库	
		pldno 出货通知单号
	action=sale_shipment_commit

返回
	
	default