<script setup>
import { computed, onMounted, ref } from 'vue'
import ClienteForm from './components/ClienteForm.vue'
import ClienteList from './components/ClienteList.vue'
import { clientesApi } from './services/clientes'

const clientes = ref([])
const buscando = ref('')
const cargando = ref(true)
const guardando = ref(false)
const mostrandoFormulario = ref(false)
const clienteEditando = ref(null)
const error = ref('')
const aviso = ref('')

const clientesFiltrados = computed(() => {
  const texto = buscando.value.trim().toLocaleLowerCase('es')
  if (!texto) return clientes.value
  return clientes.value.filter((cliente) =>
    `${cliente.nombres} ${cliente.apellidos} ${cliente.correo} ${cliente.telefono}`
      .toLocaleLowerCase('es')
      .includes(texto),
  )
})

async function cargarClientes() {
  cargando.value = true
  error.value = ''
  try {
    clientes.value = await clientesApi.listar()
  } catch (err) {
    error.value = err.message
  } finally {
    cargando.value = false
  }
}

function abrirFormulario(cliente = null) {
  clienteEditando.value = cliente
  mostrandoFormulario.value = true
  error.value = ''
}

function cerrarFormulario() {
  mostrandoFormulario.value = false
  clienteEditando.value = null
}

async function guardarCliente(datos) {
  guardando.value = true
  error.value = ''
  try {
    if (clienteEditando.value) {
      await clientesApi.actualizar(clienteEditando.value.id, datos)
      aviso.value = 'Los datos del cliente se actualizaron.'
    } else {
      await clientesApi.crear(datos)
      aviso.value = 'El cliente se agregó correctamente.'
    }
    cerrarFormulario()
    await cargarClientes()
  } catch (err) {
    error.value = err.message
  } finally {
    guardando.value = false
  }
}

async function eliminarCliente(cliente) {
  const nombre = `${cliente.nombres} ${cliente.apellidos}`
  if (!window.confirm(`¿Quieres eliminar a ${nombre}? Esta acción no se puede deshacer.`)) return

  error.value = ''
  aviso.value = ''
  try {
    await clientesApi.eliminar(cliente.id)
    aviso.value = `${nombre} se eliminó de la lista.`
    await cargarClientes()
  } catch (err) {
    error.value = err.message
  }
}

onMounted(cargarClientes)
</script>

<template>
  <div class="app-shell">
    <aside class="sidebar">
      <a class="brand" href="#" aria-label="Inicio, Clientes">
        <span class="brand-mark">
          <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M16 20v-1.5a4.5 4.5 0 0 0-4.5-4.5h-5A4.5 4.5 0 0 0 2 18.5V20m7-9a4 4 0 1 0 0-8 4 4 0 0 0 0 8Zm8-6v6m3-3h-6" /></svg>
        </span>
        <span class="brand-copy"><strong>nexo</strong><small>GESTIÓN DE CLIENTES</small></span>
      </a>

      <div class="side-label">ESPACIO DE TRABAJO</div>
      <nav aria-label="Navegación principal">
        <a class="nav-link active" href="#" aria-current="page">
          <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M16 20v-1.5a4.5 4.5 0 0 0-4.5-4.5h-5A4.5 4.5 0 0 0 2 18.5V20m7-9a4 4 0 1 0 0-8 4 4 0 0 0 0 8Zm8-5v6m3-3h-6" /></svg>
          Clientes
          <span class="nav-count">{{ clientes.length }}</span>
        </a>
      </nav>

      <div class="sidebar-bottom">
        <span class="online-dot"></span>
        <span>Tu lista está al día</span>
      </div>
    </aside>

    <main class="main-content">
      <header class="topbar">
        <span class="breadcrumb">Directorio <span>/</span> Clientes</span>
        <div class="profile">
          <span class="profile-avatar">CE</span>
          <span class="profile-name">Carlos Enriquez</span>
        </div>
      </header>

      <section class="page-content">
        <div class="page-heading">
          <div>
            <p class="eyebrow">DIRECTORIO</p>
            <h1>Clientes</h1>
            <p class="page-description">Un espacio sencillo para organizar y cuidar tus relaciones.</p>
          </div>
          <button class="button button-primary add-button" type="button" @click="abrirFormulario()">
            <svg viewBox="0 0 20 20" aria-hidden="true"><path d="M10 4v12m6-6H4" /></svg>
            Agregar cliente
          </button>
        </div>

        <div class="summary-card">
          <div class="summary-icon">
            <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M16 20v-1.5a4.5 4.5 0 0 0-4.5-4.5h-5A4.5 4.5 0 0 0 2 18.5V20m7-9a4 4 0 1 0 0-8 4 4 0 0 0 0 8Zm8-5v6m3-3h-6" /></svg>
          </div>
          <div>
            <span class="summary-label">CLIENTES REGISTRADOS</span>
            <div class="summary-value">{{ clientes.length }} <span>{{ clientes.length === 1 ? 'persona' : 'personas' }}</span></div>
          </div>
          <div class="summary-note"><span class="summary-note-dot"></span> Directorio actualizado</div>
        </div>

        <section class="directory-card" aria-labelledby="directory-title">
          <div class="directory-heading">
            <div>
              <h2 id="directory-title">Todos los clientes</h2>
              <p>Consulta y administra tu directorio.</p>
            </div>
            <label class="search-box">
              <svg viewBox="0 0 20 20" aria-hidden="true"><circle cx="8.8" cy="8.8" r="5.8" /><path d="m13 13 4 4" /></svg>
              <input v-model="buscando" type="search" placeholder="Buscar cliente..." aria-label="Buscar cliente" />
              <kbd>/</kbd>
            </label>
          </div>

          <div v-if="error" class="alert alert-error" role="alert">
            <span>{{ error }}</span>
            <button type="button" class="alert-dismiss" aria-label="Cerrar mensaje" @click="error = ''">×</button>
          </div>
          <div v-if="aviso" class="alert alert-success" role="status">
            <span>{{ aviso }}</span>
            <button type="button" class="alert-dismiss" aria-label="Cerrar mensaje" @click="aviso = ''">×</button>
          </div>

          <ClienteList
            :clientes="clientesFiltrados"
            :cargando="cargando"
            :busqueda="buscando"
            @editar="abrirFormulario"
            @eliminar="eliminarCliente"
          />
          <div v-if="!cargando && clientesFiltrados.length" class="list-footer">
            Mostrando <strong>{{ clientesFiltrados.length }}</strong> de <strong>{{ clientes.length }}</strong> clientes
          </div>
        </section>

        <footer class="page-footer">Un buen servicio comienza con conocer a las personas.</footer>
      </section>
    </main>

    <div v-if="mostrandoFormulario" class="modal-backdrop" @click.self="cerrarFormulario">
      <section class="modal-panel" role="dialog" aria-modal="true" :aria-label="clienteEditando ? 'Editar cliente' : 'Agregar cliente'">
        <ClienteForm
          :cliente="clienteEditando"
          :guardando="guardando"
          @guardar="guardarCliente"
          @cancelar="cerrarFormulario"
        />
      </section>
    </div>
  </div>
</template>
