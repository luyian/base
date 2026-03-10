import request from '@/utils/request'

export function listProcessDefinitions(params) {
  return request({
    url: '/workflow/definition/list',
    method: 'get',
    params
  })
}

export function getProcessDefinition(id) {
  return request({
    url: `/workflow/definition/${id}`,
    method: 'get'
  })
}

export function saveProcessDefinition(data) {
  return request({
    url: '/workflow/definition',
    method: 'post',
    data
  })
}

export function updateProcessDefinition(id, data) {
  return request({
    url: `/workflow/definition/${id}`,
    method: 'put',
    data
  })
}

export function deleteProcessDefinition(id) {
  return request({
    url: `/workflow/definition/${id}`,
    method: 'delete'
  })
}

export function publishProcessDefinition(id) {
  return request({
    url: `/workflow/definition/${id}/publish`,
    method: 'post'
  })
}

export function disableProcessDefinition(id) {
  return request({
    url: `/workflow/definition/${id}/disable`,
    method: 'post'
  })
}

export function startProcess(data) {
  return request({
    url: '/workflow/instance/start',
    method: 'post',
    data
  })
}

export function terminateProcess(id, comment) {
  return request({
    url: `/workflow/instance/${id}/terminate`,
    method: 'post',
    params: { comment }
  })
}

export function getMyTasks() {
  return request({
    url: '/workflow/my/tasks',
    method: 'get'
  })
}

export function getMyInitiated() {
  return request({
    url: '/workflow/my/initiated',
    method: 'get'
  })
}

export function approveTask(data) {
  return request({
    url: '/workflow/task/approve',
    method: 'post',
    data
  })
}

export function rollbackTask(data) {
  return request({
    url: '/workflow/task/rollback',
    method: 'post',
    data
  })
}

export function delegateTask(data) {
  return request({
    url: '/workflow/task/delegate',
    method: 'post',
    data
  })
}

export function getProcessHistory(id) {
  return request({
    url: `/workflow/instance/${id}/history`,
    method: 'get'
  })
}

export function getCurrentTasks(id) {
  return request({
    url: `/workflow/instance/${id}/tasks`,
    method: 'get'
  })
}

export function getProcessInstance(id) {
  return request({
    url: `/workflow/instance/${id}`,
    method: 'get'
  })
}
