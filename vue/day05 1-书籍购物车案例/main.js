const app = new Vue({
	el: '#app',
	data: {
		books: [
			{
				id: 1,
				name: '《算法导论》',
				date: '2006-9',
				price: 85.00,
				count: 1
			},
			{
				id: 2,
				name: '《UNIX编程艺术》',
				date: '2006-2',
				price: 59.00,
				count: 1
			},
			{
				id: 3,
				name: '《编程珠玑》',
				date: '2008-10',
				price: 39.00,
				count: 1
			},
			{
				id: 4,
				name: '《代码大全》',
				date: '2006-3',
				price: 128.00,
				count: 1
			}
		]
	},
	methods: {
		getFinalPrice(price) {
			return '￥' + price.toFixed(2)
		},
		decrement(index) {
			this.books[index].count--
		},
		increment(index) {
			this.books[index].count++
		},
		removeHandler(index) {
			this.books.splice(index, 1)
		}
	},
	computed: {
		totalPrice() {
			// 1 普通的for循环
			let totalPrice = 0;
			for (let i = 0; i < this.books.length; i++) {
				totalPrice += this.books[i].price * this.books[i].count;
			}
			// 2 for(let i in this.books)
			/*for(let i in this.books){
				totalPrice += this.books[i].price * this.books[i].count;
			}*/

			// 3 for(let i of this.book)
			// for (let book of this.books) {
			// 	totalPrice += book.count * book.price
			// }

			// reduce
			/*	return this.books.map(book => book.price*book.count)
						.reduce((preValue,currentValue) => preValue+currentValue)*/

		}
	},
	filters: {
		showPrice(price) {
			return '￥' + price.toFixed(2)
		}
	}
})

// 编程范式 命令式编程/声明式编程
// 编程范式 面向对象编程(第一公民：对象)/函数式编程(第一公民：函数)
// filter/map/reduce
// filter中的回调函数有一个要求 必须返回一个boolean值
// true 当返回true时 函数内部会自动将这次回调的数字加入到新的数组中
// false 当返回false时 函数的内部会过滤掉这次的数字
// 1.filter过滤函数
const nums = [2, 3, 5, 1, 77, 55, 100, 200]
//要求获取nums中大于50的数
//回调函数会遍历nums中每一个数，传入回调函数，在回调函数中写判断逻辑，返回true则会被数组接收，false会被拒绝
let newNums = nums.filter(function (num) {
	if (num > 50) {
		return true;
	}
	return false;
})
//可以使用箭头函数简写
//  let newNums = nums.filter(num => num >50)
console.log(newNums);

// 2.map高阶函数
// 要求将已经过滤的新数组每项乘以2
//map函数同样会遍历数组每一项，传入回调函数为参数，num是map遍历的每一项，回调函数function返回值会被添加到新数组中
let newNums2 = newNums.map(function (num) {
	return num * 2
})
//简写
//  let newNums2 = newNums.map(num => num * 2)
console.log(newNums2);

// 3.reduce高阶函数
// reduce作用对数组中的所有的内容进行汇总
//要求将newNums2的数组所有数累加
//reduce函数同样会遍历数组每一项，传入回调函数和‘0’为参数，0表示回调函数中preValue初始值为0，回调函数中参数preValue是每一次回调函数function返回的值，currentValue是当前值
//例如数组为[154, 110, 200, 400],则回调函数第一次返回值为0+154=154，第二次preValue为154，返回值为154+110=264，以此类推直到遍历完成
let newNum = newNums2.reduce(function (preValue, currentValue) {
	return preValue + currentValue
}, 0)
//简写
// let newNum = newNums2.reduce((preValue,currentValue) => preValue + currentValue)
console.log(newNum);

let total = nums.filter(function (n) {
	return n < 100
}).map(function (n) {
	return n * 2
}).reduce(function(preValue,currentValue){
	return preValue+currentValue
},0)

//三个需求综合
let n = nums.filter(num => num > 50).map(num => num * 2).reduce((preValue, currentValue) => preValue + currentValue)
console.log(n);