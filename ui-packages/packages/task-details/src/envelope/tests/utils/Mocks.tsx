import { TaskDetailsChannelApi, TaskDetailsEnvelopeApi } from '../../../api';
import { TaskDetailsEnvelopeViewApi } from '../../TaskDetailsEnvelopeView';
import { MessageBusServer } from '@kie-tools-core/envelope-bus/dist/api';
import { EnvelopeBusMessageManager } from '@kie-tools-core/envelope-bus/dist/common';
import { EnvelopeClient } from '@kie-tools-core/envelope-bus/dist/envelope';
import { MessageBusClientApi } from '@kie-tools-core/envelope-bus/dist/api';
import { UserTaskInstance } from '@kogito-apps/task-console-shared';

export const userTask: UserTaskInstance = {
  id: '45a73767-5da3-49bf-9c40-d533c3e77ef3',
  description: null,
  name: 'VisaApplication',
  referenceName: 'Apply for visa',
  priority: '1',
  processInstanceId: '9ae7ce3b-d49c-4f35-b843-8ac3d22fa427',
  processId: 'travels',
  rootProcessInstanceId: null,
  rootProcessId: null,
  state: 'Ready',
  actualOwner: null,
  adminGroups: [],
  adminUsers: [],
  completed: null,
  started: new Date('2020-02-19T11:11:56.282Z'),
  excludedUsers: [],
  potentialGroups: [],
  potentialUsers: [],
  inputs:
    '{"Skippable":"true","trip":{"city":"Boston","country":"US","begin":"2020-02-19T23:00:00.000+01:00","end":"2020-02-26T23:00:00.000+01:00","visaRequired":true},"TaskName":"VisaApplication","NodeName":"Apply for visa","traveller":{"firstName":"Rachel","lastName":"White","email":"rwhite@gorle.com","nationality":"Polish","address":{"street":"Cabalone","city":"Zerf","zipCode":"765756","country":"Poland"}},"Priority":"1"}',
  outputs: '{}',
  lastUpdate: new Date('2020-02-19T11:11:56.282Z'),
  endpoint:
    'http://localhost:8080/travels/9ae7ce3b-d49c-4f35-b843-8ac3d22fa427/VisaApplication/45a73767-5da3-49bf-9c40-d533c3e77ef3'
};

export const MockedMessageBusClientApi = jest.fn<
  MessageBusClientApi<TaskDetailsChannelApi>,
  []
>(() => ({
  requests: jest.fn(),
  notifications: jest.fn(),
  subscribe: jest.fn(),
  unsubscribe: jest.fn(),
  shared: jest.fn()
}));

export const MockedMessageBusServer = jest.fn<
  MessageBusServer<TaskDetailsEnvelopeApi, TaskDetailsChannelApi>,
  []
>(() => ({
  receive: jest.fn()
}));

export const MockedEnvelopeBusMessageManager = jest.fn<
  Partial<
    EnvelopeBusMessageManager<TaskDetailsEnvelopeApi, TaskDetailsChannelApi>
  >,
  []
>(() => ({
  callbacks: jest.fn(),
  remoteSubscriptions: jest.fn(),
  localSubscriptions: jest.fn(),
  send: jest.fn(),
  name: jest.fn(),
  requestIdCounter: jest.fn(),
  clientApi: new MockedMessageBusClientApi(),
  server: new MockedMessageBusServer(),
  requests: jest.fn(),
  notifications: jest.fn(),
  subscribe: jest.fn(),
  unsubscribe: jest.fn(),
  request: jest.fn(),
  notify: jest.fn(),
  respond: jest.fn(),
  callback: jest.fn(),
  receive: jest.fn(),
  getNextRequestId: jest.fn()
}));

export const MockedEnvelopeBusControllerDefinition = jest.fn<
  Partial<EnvelopeClient<TaskDetailsEnvelopeApi, TaskDetailsChannelApi>>,
  []
>(() => ({
  bus: jest.fn(),
  manager: new MockedEnvelopeBusMessageManager() as EnvelopeBusMessageManager<
    TaskDetailsEnvelopeApi,
    TaskDetailsChannelApi
  >,
  associate: jest.fn(),
  channelApi: new MockedMessageBusClientApi(),
  startListening: jest.fn(),
  stopListening: jest.fn(),
  send: jest.fn(),
  receive: jest.fn()
}));

export const MockedEnvelopeClient =
  new MockedEnvelopeBusControllerDefinition() as EnvelopeClient<
    TaskDetailsEnvelopeApi,
    TaskDetailsChannelApi
  >;

export const MockedTaskDetailsEnvelopeViewApi = jest.fn<
  TaskDetailsEnvelopeViewApi,
  []
>(() => ({
  setTask: jest.fn()
}));
