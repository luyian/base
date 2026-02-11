import request from '@/utils/request'

/**
 * 绑定飞书账号
 */
export function bindFeishu(data) {
    return request({
        url: '/system/feishu/bind',
        method: 'post',
        data
    })
}

/**
 * 解绑飞书账号
 */
export function unbindFeishu() {
    return request({
        url: '/system/feishu/unbind',
        method: 'delete'
    })
}

/**
 * 查询飞书绑定信息
 */
export function getFeishuBindInfo() {
    return request({
        url: '/system/feishu/bindInfo',
        method: 'get'
    })
}

/**
 * 发送飞书消息
 */
export function sendFeishuMessage(data) {
    return request({
        url: '/system/feishu/send',
        method: 'post',
        data
    })
}

/**
 * 发送飞书消息给系统用户
 */
export function sendFeishuMessageToUser(userId, data) {
    return request({
        url: `/system/feishu/send/user/${userId}`,
        method: 'post',
        data
    })
}

/**
 * 上传图片到飞书
 */
export function uploadFeishuImage(file) {
    const formData = new FormData()
    formData.append('file', file)
    return request({
        url: '/system/feishu/upload/image',
        method: 'post',
        data: formData,
        headers: { 'Content-Type': 'multipart/form-data' }
    })
}

/**
 * 上传文件到飞书
 */
export function uploadFeishuFile(file, fileType = 'stream') {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('fileType', fileType)
    return request({
        url: '/system/feishu/upload/file',
        method: 'post',
        data: formData,
        headers: { 'Content-Type': 'multipart/form-data' }
    })
}
