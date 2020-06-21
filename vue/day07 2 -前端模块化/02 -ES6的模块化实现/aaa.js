var name = '小明'
var age = 18
var flag = true

function sum(num1, num2) {
	return num1 + num2;
}

if (flag) {
	console.log(sum(20, 30))
}

// 1 导入方式1
export {
	flag, sum
}

// 2 导出方式2
export var num1 = 1000;
export var height = 180;

// 3 导出函数/类
export function mu1(num1, num2) {
	return num1 + num2
}

export class Person {
	run() {
		console.log('在奔跑');
	}
}

// 4 export default
const address = '北京市'

// 一个js文件只能有一个default export
// export default address
export default function (argument) {
	console.log(argument)
}