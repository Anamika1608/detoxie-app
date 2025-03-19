// Reexport the native module. On web, it will be resolved to DetoxieModule.web.ts
// and on native platforms to DetoxieModule.ts
export { default } from './DetoxieModule';
export { default as DetoxieModuleView } from './DetoxieModuleView';
export * from  './DetoxieModule.types';
