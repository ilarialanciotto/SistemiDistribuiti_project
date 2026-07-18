export type State = 'start' | 'ai_analyzed' | 'sent' | 'stopped' | 'in_progress' | 'done';
export type Category = 'network' | 'database' | 'bug' | 'configuration' | 'other';
export type Level = 'low' | 'medium' | 'high';
export const ALL_STATES: State[] = ['start', 'ai_analyzed', 'sent', 'stopped', 'in_progress', 'done'];
export type Problem = 'software' | 'hardware' | 'web_service' | 'network_configuration' | 'generic';

export interface TicketDTO {
  id : number;
  problem_title: string;
  description: string;
  problem?: string;
  urgency_percepite: string;
}

