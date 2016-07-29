#Amphenol_android开发接口文档

##书写说明
- api采用json数据格式返回
- 为做说明，采用 keyName ：valueTyep 形式说明jaon键值对类型，如：code ： int	返回码 ，从左往右依次为 key名称、value数据类型、value描述。

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

##查询送货单
	请求方式：get
	参数：delive_code	供应商送货单号码
	action=query_receipt

返回

	firm:String	送货厂商
	receipt_number:String 送货单号码
	status_code:int	收货单状态
		10：未收货
		40：部分收货
		50：收货完成
	mater_list:List<Map<String,Object>> 物料集合
		Map<String,Object>
			mater_po: String 采购单-项次
			number: String 收货单行号
			mate: String 物料编号
			quantity： Double 数量
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

##查询物料明细
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
	status：int 状态
		10：未收货
		50：已收货
		60：已关闭
	actual_single:double 实际单重
	actual_unit:String 实际单重单位
	actual_quantity:double 实际总数
	shard:String 收货子库
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

##确认物料收货
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



##创建调拨单-查询物料列表
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

##创建调拨单-查询物料明细
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
	
##创建调拨单-提交
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
	
	code 
		6 目标库位不存在

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
	code = 2 目标库位不存在

##审核调拨单-终止过账

	请求方式：get
	参数：
		requisition 调拨单号
		requisition_line 调拨单行	
	action=check_requisition_cancel

返回

	default
		