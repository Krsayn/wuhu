// 1 导出的{}中定义的变量
import {flag, sum} from "./aaa.js";

if (flag) {
	console.log('小明是天才 哈哈哈');
	console.log(sum(20, 30))
}

// 2 直接导入export定义的变量
import {num1, height} from "./aaa.js";

console.log(num1)
console.log(height)

// 3 导入 export的function
import {mu1, Person} from "./aaa.js";

console.log(mu1(1, 8))

const person = new Person()
person.run()

import add from "./aaa.js"

console.log(add('你好ffdsafasf	啊'))