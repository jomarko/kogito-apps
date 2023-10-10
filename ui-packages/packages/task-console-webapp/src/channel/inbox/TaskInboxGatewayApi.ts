import { QueryFilter, SortBy, TaskInboxState } from '@kogito-apps/task-inbox';
import { UserTaskInstance } from '@kogito-apps/task-console-shared';
import { TaskInboxQueries } from './TaskInboxQueries';
import { User } from '@kogito-apps/consoles-common/dist/environment/auth';

export interface TaskInboxGatewayApi {
  taskInboxState: TaskInboxState;
  setInitialState: (initialState: TaskInboxState) => Promise<void>;
  applyFilter(filter: QueryFilter): Promise<void>;
  applySorting(sortBy: SortBy): Promise<void>;
  query(offset: number, limit: number): Promise<UserTaskInstance[]>;
  getTaskById(uuid: string): Promise<UserTaskInstance>;
  openTask: (userTask: UserTaskInstance) => void;
  clearOpenTask: () => Promise<void>;

  onOpenTaskListen: (listener: OnOpenTaskListener) => UnSubscribeHandler;
}

export interface OnOpenTaskListener {
  onOpen: (userTask: UserTaskInstance) => void;
}

export interface UnSubscribeHandler {
  unSubscribe: () => void;
}

export class TaskInboxGatewayApiImpl implements TaskInboxGatewayApi {
  private readonly listeners: OnOpenTaskListener[] = [];
  private readonly user: User;
  private readonly queries: TaskInboxQueries;
  private _taskInboxState: TaskInboxState;
  private activeTask: UserTaskInstance;

  constructor(user: User, queries: TaskInboxQueries) {
    this.user = user;
    this.queries = queries;
  }

  get taskInboxState(): TaskInboxState {
    return this._taskInboxState;
  }

  setInitialState(taskInboxState: TaskInboxState): Promise<void> {
    this._taskInboxState = taskInboxState;
    return Promise.resolve();
  }

  public clearOpenTask(): Promise<void> {
    this.activeTask = null;
    return Promise.resolve();
  }

  openTask(task: UserTaskInstance): Promise<void> {
    this.activeTask = task;
    this.listeners.forEach((listener) => listener.onOpen(task));
    return Promise.resolve();
  }

  applyFilter(filter: QueryFilter): Promise<void> {
    this._taskInboxState.filters = filter;
    return Promise.resolve();
  }

  applySorting(sortBy: SortBy): Promise<void> {
    this._taskInboxState.sortBy = sortBy;
    return Promise.resolve();
  }

  getTaskById(taskId: string): Promise<UserTaskInstance> {
    if (this.activeTask && this.activeTask.id === taskId) {
      return Promise.resolve(this.activeTask);
    }
    return this.queries.getUserTaskById(taskId);
  }

  query(offset: number, limit: number): Promise<UserTaskInstance[]> {
    return new Promise<UserTaskInstance[]>((resolve, reject) => {
      this.queries
        .getUserTasks(
          this.user,
          offset,
          limit,
          this._taskInboxState.filters,
          this._taskInboxState.sortBy
        )
        .then((value) => {
          this._taskInboxState.currentPage = { offset, limit };
          resolve(value);
        })
        .catch((reason) => {
          reject(reason);
        });
    });
  }

  onOpenTaskListen(listener: OnOpenTaskListener): UnSubscribeHandler {
    this.listeners.push(listener);

    const unSubscribe = () => {
      const index = this.listeners.indexOf(listener);
      if (index > -1) {
        this.listeners.splice(index, 1);
      }
    };

    return {
      unSubscribe
    };
  }
}
