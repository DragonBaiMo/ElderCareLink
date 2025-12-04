<template>
  <router-view v-slot="{ Component }">
    <transition name="fade-slide" mode="out-in">
      <component :is="Component" />
    </transition>
  </router-view>
</template>

<style>
/* Ensure full height for the app */
#app {
  height: 100vh;
  width: 100vw;
  overflow: hidden;
}
</style>