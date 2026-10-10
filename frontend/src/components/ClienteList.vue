<script setup>
defineProps({
  clientes: {
    type: Array,
    default: () => [],
  },
  cargando: {
    type: Boolean,
    default: false,
  },
  busqueda: {
    type: String,
    default: '',
  },
})

defineEmits(['editar', 'eliminar'])

function iniciales(cliente) {
  return `${cliente.nombres.charAt(0)}${cliente.apellidos.charAt(0)}`.toUpperCase()
}

function fechaCorta(fecha) {
  return new Intl.DateTimeFormat('es-MX', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
  }).format(new Date(fecha))
}
</script>

<template>
  <div class="table-wrap">
    <table v-if="clientes.length">
      <thead>
        <tr>
          <th>Cliente</th>
          <th>Correo electrónico</th>
          <th>Teléfono</th>
          <th>Fecha de registro</th>
          <th><span class="visually-hidden">Acciones</span></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="cliente in clientes" :key="cliente.id">
          <td>
            <div class="person-cell">
              <span class="avatar">{{ iniciales(cliente) }}</span>
              <span class="person-name">{{ cliente.nombres }} {{ cliente.apellidos }}</span>
            </div>
          </td>
          <td class="muted-cell">{{ cliente.correo }}</td>
          <td class="muted-cell">{{ cliente.telefono }}</td>
          <td class="muted-cell">{{ fechaCorta(cliente.fechaCreacion) }}</td>
          <td>
            <div class="row-actions">
              <button class="icon-button" type="button" :aria-label="`Editar a ${cliente.nombres}`" @click="$emit('editar', cliente)">
                <svg viewBox="0 0 20 20" aria-hidden="true"><path d="m13.5 3.5 3 3M4 16l3.8-.8L16 7l-3-3-8.2 8.2L4 16Z" /></svg>
              </button>
              <button class="icon-button danger-action" type="button" :aria-label="`Eliminar a ${cliente.nombres}`" @click="$emit('eliminar', cliente)">
                <svg viewBox="0 0 20 20" aria-hidden="true"><path d="M4 6h12M8 6V4h4v2m2 0-.7 10H6.7L6 6m2.5 3v4m3-4v4" /></svg>
              </button>
            </div>
          </td>
        </tr>
      </tbody>
    </table>

    <div v-else-if="cargando" class="empty-state">
      <span class="loader" aria-hidden="true"></span>
      <h3>Cargando clientes</h3>
      <p>Estamos actualizando la lista.</p>
    </div>
    <div v-else class="empty-state">
      <span class="empty-icon" aria-hidden="true">
        <svg viewBox="0 0 32 32"><path d="M21 27v-2a5 5 0 0 0-5-5H9a5 5 0 0 0-5 5v2m7.5-11a5 5 0 1 0 0-10 5 5 0 0 0 0 10ZM22 11h7m-3.5-3.5v7" /></svg>
      </span>
      <h3>{{ busqueda ? 'No encontramos resultados' : 'Aún no hay clientes' }}</h3>
      <p>{{ busqueda ? 'Prueba con otro nombre o correo.' : 'Agrega tu primer cliente para empezar.' }}</p>
    </div>
  </div>
</template>
