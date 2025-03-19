import { registerWebModule, NativeModule } from 'expo';

import { DetoxieModuleEvents } from './DetoxieModule.types';

class DetoxieModule extends NativeModule<DetoxieModuleEvents> {
  PI = Math.PI;
  async setValueAsync(value: string): Promise<void> {
    this.emit('onChange', { value });
  }
  hello() {
    return 'Hello world! 👋';
  }
}

export default registerWebModule(DetoxieModule);
