import {Category, Level, State , Problem} from './ticketModel.model';
import {AttachmentDTO} from './attachment.model';

export interface TicketAdminDTO {
  id : number;
  problem_title: string;
  description: string;
  problem: Problem;
  urgency_percepite: Level;
  priority_level_AI: number;
  start_date: string;
  category_AI: Category | null;
  keyword_AI: string | null;
  state: State;
  attachment?: AttachmentDTO | null;
}
