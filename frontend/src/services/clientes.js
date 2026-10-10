async function request(path, options = {}) {
  const response = await fetch(path, {
    ...options,
    headers: {
      ...(options.body ? { 'Content-Type': 'application/json' } : {}),
      ...options.headers,
    },
  })

  if (!response.ok) {
    let message = 'No se pudo completar la solicitud'
    try {
      const body = await response.json()
      message = body.mensaje || message
      if (body.errores) {
        message = Object.values(body.errores).join('. ')
      }
    } catch {
      message = `Error del servidor (${response.status})`
    }
    throw new Error(message)
  }

  if (response.status === 204) return null
  return response.json()
}

export const clientesApi = {
  listar: () => request('/clientes'),
  crear: (cliente) => request('/clientes', {
    method: 'POST',
    body: JSON.stringify(cliente),
  }),
  actualizar: (id, cliente) => request(`/clientes/${id}`, {
    method: 'PUT',
    body: JSON.stringify(cliente),
  }),
  eliminar: (id) => request(`/clientes/${id}`, {
    method: 'DELETE',
  }),
}
