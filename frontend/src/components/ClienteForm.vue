<script setup>
import { reactive, watch } from 'vue'

const props = defineProps({
  cliente: {
    type: Object,
    default: null,
  },
  guardando: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits(['guardar', 'cancelar'])

const formulario = reactive({
  nombres: '',
  apellidos: '',
  correo: '',
  telefono: '',
})

watch(
  () => props.cliente,
  (cliente) => {
    formulario.nombres = cliente?.nombres || ''
    formulario.apellidos = cliente?.apellidos || ''
    formulario.correo = cliente?.correo || ''
    formulario.telefono = cliente?.telefono || ''
  },
  { immediate: true },
)

function guardar() {
  emit('guardar', { ...formulario })
}
</script>

<template>
  <form class="customer-form" @submit.prevent="guardar">
    <div class="form-heading">
      <div>
        <p class="eyebrow">{{ cliente ? 'ACTUALIZAR DATOS' : 'NUEVO REGISTRO' }}</p>
        <h2>{{ cliente ? 'Editar cliente' : 'Agregar cliente' }}</h2>
      </div>
      <button class="icon-button close-button" type="button" aria-label="Cerrar formulario" @click="emit('cancelar')">
        <svg viewBox="0 0 20 20" aria-hidden="true"><path d="m5 5 10 10M15 5 5 15" /></svg>
      </button>
    </div>

    <label class="field">
      <span>Nombres</span>
      <input v-model.trim="formulario.nombres" name="nombres" maxlength="100" autocomplete="given-name" placeholder="Ej. Ana" required />
    </label>
    <label class="field">
      <span>Apellidos</span>
      <input v-model.trim="formulario.apellidos" name="apellidos" maxlength="100" autocomplete="family-name" placeholder="Ej. García" required />
    </label>
    <label class="field">
      <span>Correo electrónico</span>
      <input v-model.trim="formulario.correo" name="correo" type="email" maxlength="150" autocomplete="email" placeholder="ana@correo.com" required />
    </label>
    <label class="field">
      <span>Teléfono</span>
      <input v-model.trim="formulario.telefono" name="telefono" type="tel" maxlength="30" autocomplete="tel" placeholder="+52 55 1234 5678" required />
    </label>

    <div class="form-actions">
      <button class="button button-quiet" type="button" :disabled="guardando" @click="emit('cancelar')">Cancelar</button>
      <button class="button button-primary" type="submit" :disabled="guardando">
        <span v-if="guardando" class="button-spinner" aria-hidden="true"></span>
        {{ guardando ? 'Guardando...' : cliente ? 'Guardar cambios' : 'Agregar cliente' }}
      </button>
    </div>
  </form>
</template>
