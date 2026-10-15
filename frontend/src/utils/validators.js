// 身份证号校验（GB 11643-1999，18 位）
// 规则：格式 17位数字+校验位，出生日期合法，末位校验码正确。允许为空（填了才校验）。
export function validateIdCard(rule, value, callback) {
  if (!value) return callback() // 允许为空
  const id = String(value).toUpperCase().trim()
  // 1) 基本格式：17位数字 + 数字或X
  if (!/^\d{17}[\dX]$/.test(id)) {
    return callback(new Error('身份证号格式不正确（应为18位）'))
  }
  // 2) 出生日期合法性
  const year = +id.substring(6, 10)
  const month = +id.substring(10, 12)
  const day = +id.substring(12, 14)
  const birth = new Date(year, month - 1, day)
  if (birth.getFullYear() !== year || birth.getMonth() !== month - 1 || birth.getDate() !== day) {
    return callback(new Error('身份证号出生日期无效'))
  }
  // 3) 校验码（ISO 7064 MOD 11-2）
  const weights = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2]
  const codes = ['1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2']
  let sum = 0
  for (let i = 0; i < 17; i++) sum += +id[i] * weights[i]
  if (codes[sum % 11] !== id[17]) {
    return callback(new Error('身份证号校验位不正确'))
  }
  callback()
}
